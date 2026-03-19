package com.example.payment.application.service;

import com.example.payment.application.dto.CreatePaymentOrderRequest;
import com.example.payment.application.dto.CashierOrderResponse;
import com.example.payment.application.dto.CreateCashierOrderRequest;
import com.example.payment.application.dto.PaymentOrderListItem;
import com.example.payment.application.dto.PaymentOrderResponse;
import com.example.payment.domain.enums.PaymentEventType;
import com.example.payment.domain.enums.PaymentPlatform;
import com.example.payment.domain.enums.PaymentStatus;
import com.example.payment.domain.model.PaymentOrder;
import com.example.payment.domain.state.PaymentStateMachine;
import com.example.payment.infrastructure.channel.ChannelClientFactory;
import com.example.payment.infrastructure.channel.model.ChannelUnifiedOrderRequest;
import com.example.payment.infrastructure.channel.model.ChannelUnifiedOrderResponse;
import com.example.payment.infrastructure.mq.PaymentEventPublisher;
import com.example.payment.infrastructure.mq.PaymentOrderChangedMessage;
import com.example.payment.infrastructure.persistence.mapper.PayOrderStatusLogMapper;
import com.example.payment.infrastructure.repository.PaymentOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;

/**
 * 支付订单应用服务。
 * 负责组装领域对象、调用渠道、持久化订单、记录状态日志与发布领域事件。
 */
@Service
public class PaymentApplicationService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final PayOrderStatusLogMapper payOrderStatusLogMapper;
    private final ChannelRoutingService channelRoutingService;
    private final ChannelClientFactory channelClientFactory;
    private final PaymentStateMachine paymentStateMachine = new PaymentStateMachine();

    public PaymentApplicationService(PaymentOrderRepository paymentOrderRepository,
                                     PaymentEventPublisher paymentEventPublisher,
                                     PayOrderStatusLogMapper payOrderStatusLogMapper,
                                     ChannelRoutingService channelRoutingService,
                                     ChannelClientFactory channelClientFactory) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.paymentEventPublisher = paymentEventPublisher;
        this.payOrderStatusLogMapper = payOrderStatusLogMapper;
        this.channelRoutingService = channelRoutingService;
        this.channelClientFactory = channelClientFactory;
    }

    /**
     * 创建普通支付订单并自动提交到支付中状态。
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentOrderResponse createOrder(CreatePaymentOrderRequest request) {
        String orderNo = "P" + System.currentTimeMillis() + (int) (Math.random() * 1000);
        PaymentOrder order = PaymentOrder.create(
                orderNo,
                request.getMerchantNo(),
                request.getAppId(),
                request.getMerchantOrderNo(),
                request.getPayScene(),
                request.getPayWay(),
                request.getAmount(),
                request.getSubject(),
                request.getNotifyUrl(),
                request.getReturnUrl(),
                request.getCurrency());

        order.setChannelCode(selectChannel(order));
        paymentOrderRepository.save(order);

        PaymentStatus from = order.getStatus();
        PaymentStatus to = paymentStateMachine.transit(from, PaymentEventType.SUBMIT);
        order.markStatus(to);
        paymentOrderRepository.save(order);

        payOrderStatusLogMapper.insert(order.getOrderNo(), order.getMerchantNo(), from.name(), to.name(),
                PaymentEventType.SUBMIT.name(), "create-order-auto-submit", "system", LocalDateTime.now());

        publishOrderChanged(order, PaymentEventType.SUBMIT);
        return PaymentOrderResponse.of(order.getOrderNo(), order.getChannelCode(), order.getStatus());
    }

    /**
     * 创建收银台订单。
     * 1. 幂等判断（同商户单号直接返回）
     * 2. 解析可用渠道并按优先级依次尝试
     * 3. 渠道下单成功后更新订单并推进状态机
     */
    @Transactional(rollbackFor = Exception.class)
    public CashierOrderResponse createCashierOrder(CreateCashierOrderRequest request) {
        PaymentOrder exists = paymentOrderRepository
                .findByMerchantOrder(request.getMerchantNo(), request.getAppId(), request.getMerchantOrderNo())
                .orElse(null);
        if (exists != null) {
            return toCashierResponse(exists, request.getPlatform(), null);
        }

        List<ChannelRoutingService.ChannelRouteResult> routeCandidates =
                channelRoutingService.resolveCandidates(request.getPlatform(), request.getChannelCode());

        String orderNo = "P" + System.currentTimeMillis() + (int) (Math.random() * 1000);
        PaymentOrder order = PaymentOrder.create(
                orderNo,
                request.getMerchantNo(),
                request.getAppId(),
                request.getMerchantOrderNo(),
                "POS",
                request.getPlatform().name(),
                request.getAmount(),
                request.getSubject(),
                request.getNotifyUrl(),
                request.getReturnUrl(),
                request.getCurrency()
        );
        order.setChannelCode(routeCandidates.get(0).channelCode());
        paymentOrderRepository.save(order);

        ChannelUnifiedOrderResponse channelResponse = null;
        RuntimeException lastEx = null;
        List<String> failedChannels = new ArrayList<>();
        for (ChannelRoutingService.ChannelRouteResult route : routeCandidates) {
            try {
                ChannelUnifiedOrderRequest unifiedOrderRequest = new ChannelUnifiedOrderRequest();
                unifiedOrderRequest.setOrderNo(orderNo);
                unifiedOrderRequest.setMerchantOrderNo(order.getMerchantOrderNo());
                unifiedOrderRequest.setMerchantNo(order.getMerchantNo());
                unifiedOrderRequest.setAppId(order.getAppId());
                unifiedOrderRequest.setPlatform(request.getPlatform());
                unifiedOrderRequest.setChannelCode(route.channelCode());
                unifiedOrderRequest.setDeviceNo(request.getDeviceNo());
                unifiedOrderRequest.setAmount(order.getAmount());
                unifiedOrderRequest.setCurrency(order.getCurrency());
                unifiedOrderRequest.setSubject(order.getSubject());
                unifiedOrderRequest.setNotifyUrl(order.getNotifyUrl());
                unifiedOrderRequest.setReturnUrl(order.getReturnUrl());
                unifiedOrderRequest.setChannelConfig(route.apiConfig());

                channelResponse = channelClientFactory
                        .get(request.getPlatform())
                        .unifiedOrder(unifiedOrderRequest);
                order.setChannelCode(route.channelCode());
                break;
            } catch (RuntimeException ex) {
                lastEx = ex;
                failedChannels.add(route.channelCode() + ": " + simplifyErrorMessage(ex));
            }
        }
        if (channelResponse == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "\u6240\u6709\u6e20\u9053\u4e0b\u5355\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5\u6e20\u9053\u914d\u7f6e\u3002\u5931\u8d25\u660e\u7ec6: " + String.join(" | ", failedChannels),
                    lastEx
            );
        }

        order.setChannelOrderNo(channelResponse.getChannelOrderNo());
        paymentOrderRepository.save(order);

        PaymentStatus from = order.getStatus();
        PaymentStatus to = paymentStateMachine.transit(from, PaymentEventType.SUBMIT);
        order.markStatus(to);
        paymentOrderRepository.save(order);

        payOrderStatusLogMapper.insert(order.getOrderNo(), order.getMerchantNo(), from.name(), to.name(),
                PaymentEventType.SUBMIT.name(), "cashier-submit", request.getDeviceNo(), LocalDateTime.now());

        publishOrderChanged(order, PaymentEventType.SUBMIT);
        return toCashierResponse(order, request.getPlatform(), channelResponse);
    }

    /**
     * 分页查询订单列表（供管理台展示）。
     */
    public List<PaymentOrderListItem> listOrders(String merchantNo, String appId, int pageNo, int pageSize) {
        return paymentOrderRepository.findPageByMerchant(merchantNo, appId, pageNo, pageSize)
                .stream()
                .map(order -> PaymentOrderListItem.of(
                        order.getOrderNo(),
                        order.getMerchantOrderNo(),
                        order.getMerchantNo(),
                        order.getAppId(),
                        parsePlatform(order.getPayWay()),
                        order.getChannelCode(),
                        order.getAmount(),
                        order.getStatus(),
                        order.getCreatedAt()))
                .toList();
    }

    /**
     * 对订单应用状态事件并落库。
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentOrderResponse applyEvent(String orderNo, PaymentEventType eventType, String operator) {
        PaymentOrder order = paymentOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderNo));

        PaymentStatus from = order.getStatus();
        PaymentStatus targetStatus = paymentStateMachine.transit(from, eventType);
        order.markStatus(targetStatus);
        paymentOrderRepository.save(order);

        payOrderStatusLogMapper.insert(order.getOrderNo(), order.getMerchantNo(), from.name(), targetStatus.name(),
                eventType.name(), null, operator, LocalDateTime.now());

        publishOrderChanged(order, eventType);
        return PaymentOrderResponse.of(order.getOrderNo(), order.getChannelCode(), order.getStatus());
    }

    /**
     * 发布订单变更事件（Outbox/MQ实现由基础设施层决定）。
     */
    private void publishOrderChanged(PaymentOrder order, PaymentEventType eventType) {
        paymentEventPublisher.publish(PaymentOrderChangedMessage.of(
                UUID.randomUUID().toString(),
                eventType.name(),
                order.getOrderNo(),
                order.getMerchantNo(),
                order.getChannelCode(),
                order.getAmount(),
                order.getStatus(),
                LocalDateTime.now()));
    }

    /**
     * 示例路由策略：按金额走不同默认渠道。
     */
    private String selectChannel(PaymentOrder order) {
        return order.getAmount().doubleValue() <= 1000 ? "WX_JSAPI" : "ALI_QR";
    }

    /**
     * 取最底层异常信息，便于返回给前端定位渠道失败原因。
     */
    private String simplifyErrorMessage(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String msg = current.getMessage();
        return (msg == null || msg.isBlank()) ? current.getClass().getSimpleName() : msg;
    }

    /**
     * 组装收银台返回参数（包含支付链接/二维码内容）。
     */
    private CashierOrderResponse toCashierResponse(PaymentOrder order,
                                                   PaymentPlatform platform,
                                                   ChannelUnifiedOrderResponse channelResponse) {
        String payUrl = channelResponse == null ? "https://cashier.mock/pay/" + order.getOrderNo() : channelResponse.getPayUrl();
        String qrCode = channelResponse == null
                ? "pay://" + order.getChannelCode() + "/" + order.getOrderNo()
                : channelResponse.getQrCodeContent();
        return CashierOrderResponse.of(
                order.getOrderNo(),
                order.getMerchantOrderNo(),
                platform,
                order.getChannelCode(),
                order.getAmount(),
                order.getStatus(),
                payUrl,
                qrCode
        );
    }

    /**
     * 历史数据兼容：非法值兜底为微信平台。
     */
    private PaymentPlatform parsePlatform(String payWay) {
        try {
            return PaymentPlatform.valueOf(payWay);
        } catch (Exception ex) {
            return PaymentPlatform.WECHAT;
        }
    }
}

