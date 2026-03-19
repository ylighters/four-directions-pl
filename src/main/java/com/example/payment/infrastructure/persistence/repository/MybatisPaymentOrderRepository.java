package com.example.payment.infrastructure.persistence.repository;

import com.example.payment.domain.enums.PaymentStatus;
import com.example.payment.domain.model.PaymentOrder;
import com.example.payment.infrastructure.persistence.MoneyHelper;
import com.example.payment.infrastructure.persistence.mapper.PayOrderIndexMapper;
import com.example.payment.infrastructure.persistence.po.PayOrderPO;
import com.example.payment.infrastructure.persistence.mapper.PayOrderMapper;
import com.example.payment.infrastructure.repository.PaymentOrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 基于 MyBatis 的支付订单仓储实现。
 * 负责领域对象与持久化对象之间的映射，以及并发更新控制。
 */
@Repository
public class MybatisPaymentOrderRepository implements PaymentOrderRepository {

    private final PayOrderMapper payOrderMapper;
    private final PayOrderIndexMapper payOrderIndexMapper;

    public MybatisPaymentOrderRepository(PayOrderMapper payOrderMapper, PayOrderIndexMapper payOrderIndexMapper) {
        this.payOrderMapper = payOrderMapper;
        this.payOrderIndexMapper = payOrderIndexMapper;
    }

    @Override
    public void save(PaymentOrder order) {
        // 新订单：写主表 + 写索引表（用于按 orderNo 快速反查商户分片键）。
        if (order.getId() == null) {
            PayOrderPO insertDO = toDO(order);
            payOrderMapper.insert(insertDO);
            payOrderIndexMapper.insert(order.getOrderNo(), order.getMerchantNo(), order.getAppId());
            order.setId(insertDO.getId());
            return;
        }

        // 老订单：乐观锁更新，避免并发覆盖。
        PayOrderPO updateDO = toDO(order);
        int updated = payOrderMapper.updateStatusWithVersion(updateDO);
        if (updated <= 0) {
            throw new IllegalStateException("Concurrent update detected for orderNo=" + order.getOrderNo());
        }
        order.setVersion(order.getVersion() + 1);
    }

    @Override
    public Optional<PaymentOrder> findByOrderNo(String orderNo) {
        // 先走索引表拿 merchantNo，再命中分片主表。
        String merchantNo = payOrderIndexMapper.selectMerchantNoByOrderNo(orderNo);
        if (merchantNo == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(payOrderMapper.selectByOrderNo(orderNo, merchantNo)).map(this::toDomain);
    }

    @Override
    public Optional<PaymentOrder> findByMerchantOrder(String merchantNo, String appId, String merchantOrderNo) {
        return Optional.ofNullable(payOrderMapper.selectByMerchantOrder(merchantNo, appId, merchantOrderNo))
                .map(this::toDomain);
    }

    @Override
    public List<PaymentOrder> findPageByMerchant(String merchantNo, String appId, int pageNo, int pageSize) {
        int safePageNo = Math.max(pageNo, 1);
        int safePageSize = Math.max(1, Math.min(pageSize, 200));
        int offset = (safePageNo - 1) * safePageSize;
        return payOrderMapper.selectPageByMerchant(merchantNo, appId, offset, safePageSize)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 领域对象 -> 持久化对象。
     */
    private PayOrderPO toDO(PaymentOrder order) {
        PayOrderPO orderDO = new PayOrderPO();
        orderDO.setId(order.getId());
        orderDO.setOrderNo(order.getOrderNo());
        orderDO.setMerchantNo(order.getMerchantNo());
        orderDO.setAppId(order.getAppId());
        orderDO.setMerchantOrderNo(order.getMerchantOrderNo());
        orderDO.setPayScene(order.getPayScene());
        orderDO.setPayWay(order.getPayWay());
        orderDO.setChannelCode(order.getChannelCode());
        orderDO.setChannelOrderNo(order.getChannelOrderNo());
        orderDO.setAmount(MoneyHelper.toCent(order.getAmount()));
        orderDO.setCurrency(order.getCurrency());
        orderDO.setStatus(order.getStatus().name());
        orderDO.setSubject(order.getSubject());
        orderDO.setNotifyUrl(order.getNotifyUrl());
        orderDO.setReturnUrl(order.getReturnUrl());
        orderDO.setVersion(order.getVersion());
        return orderDO;
    }

    /**
     * 持久化对象 -> 领域对象。
     */
    private PaymentOrder toDomain(PayOrderPO orderDO) {
        PaymentOrder order = new PaymentOrder();
        order.setId(orderDO.getId());
        order.setOrderNo(orderDO.getOrderNo());
        order.setMerchantNo(orderDO.getMerchantNo());
        order.setAppId(orderDO.getAppId());
        order.setMerchantOrderNo(orderDO.getMerchantOrderNo());
        order.setPayScene(orderDO.getPayScene());
        order.setPayWay(orderDO.getPayWay());
        order.setChannelCode(orderDO.getChannelCode());
        order.setChannelOrderNo(orderDO.getChannelOrderNo());
        order.setAmount(MoneyHelper.toYuan(orderDO.getAmount()));
        order.setCurrency(orderDO.getCurrency());
        order.setStatus(PaymentStatus.valueOf(orderDO.getStatus()));
        order.setSubject(orderDO.getSubject());
        order.setNotifyUrl(orderDO.getNotifyUrl());
        order.setReturnUrl(orderDO.getReturnUrl());
        order.setVersion(orderDO.getVersion());
        order.setCreatedAt(orderDO.getCreatedAt());
        order.setUpdatedAt(orderDO.getUpdatedAt());
        return order;
    }
}

