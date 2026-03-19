package com.example.payment.infrastructure.mq;

public interface PaymentEventPublisher {

    void publish(PaymentOrderChangedMessage message);
}

