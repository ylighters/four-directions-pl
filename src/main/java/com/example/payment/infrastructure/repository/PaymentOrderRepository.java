package com.example.payment.infrastructure.repository;

import com.example.payment.domain.model.PaymentOrder;

import java.util.List;
import java.util.Optional;

/**
 * 支付订单仓储抽象。
 * 隔离领域层与具体持久化实现（MyBatis、分库分表等）。
 */
public interface PaymentOrderRepository {

    /**
     * 保存订单（新增或更新）。
     */
    void save(PaymentOrder order);

    /**
     * 按平台订单号查询。
     */
    Optional<PaymentOrder> findByOrderNo(String orderNo);

    /**
     * 按商户维度唯一键查询，用于幂等控制。
     */
    Optional<PaymentOrder> findByMerchantOrder(String merchantNo, String appId, String merchantOrderNo);

    /**
     * 按商户分页查询订单。
     */
    List<PaymentOrder> findPageByMerchant(String merchantNo, String appId, int pageNo, int pageSize);
}

