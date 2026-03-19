package com.example.payment.infrastructure.mq;

/**
 * 支付事件发布器抽象。
 * 默认实现为 Outbox 持久化，后续可扩展为直连 MQ。
 */
public interface PaymentEventPublisher {

    /**
     * 发布订单变更事件。
     */
    void publish(PaymentOrderChangedMessage message);
}

