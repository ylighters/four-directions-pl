package com.example.payment.application.service;

import com.example.payment.application.dto.CreatePaymentOrderRequest;
import com.example.payment.application.dto.PaymentOrderResponse;
import com.example.payment.domain.enums.PaymentEventType;
import com.example.payment.domain.enums.PaymentStatus;
import com.example.payment.domain.model.PaymentOrder;
import com.example.payment.domain.state.PaymentStateMachine;
import com.example.payment.infrastructure.mq.PaymentEventPublisher;
import com.example.payment.infrastructure.mq.PaymentOrderChangedMessage;
import com.example.payment.infrastructure.persistence.mapper.PayOrderStatusLogMapper;
import com.example.payment.infrastructure.repository.PaymentOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentApplicationService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final PayOrderStatusLogMapper payOrderStatusLogMapper;
    private final PaymentStateMachine paymentStateMachine = new PaymentStateMachine();

    public PaymentApplicationService(PaymentOrderRepository paymentOrderRepository,
                                     PaymentEventPublisher paymentEventPublisher,
                                     PayOrderStatusLogMapper payOrderStatusLogMapper) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.paymentEventPublisher = paymentEventPublisher;
        this.payOrderStatusLogMapper = payOrderStatusLogMapper;
    }

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

    private String selectChannel(PaymentOrder order) {
        return order.getAmount().doubleValue() <= 1000 ? "WX_JSAPI" : "ALI_QR";
    }
}

