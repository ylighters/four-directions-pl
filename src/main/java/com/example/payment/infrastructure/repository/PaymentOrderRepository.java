package com.example.payment.infrastructure.repository;

import com.example.payment.domain.model.PaymentOrder;

import java.util.Optional;

public interface PaymentOrderRepository {

    void save(PaymentOrder order);

    Optional<PaymentOrder> findByOrderNo(String orderNo);
}

