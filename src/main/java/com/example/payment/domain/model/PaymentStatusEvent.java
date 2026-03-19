package com.example.payment.domain.model;

import com.example.payment.domain.enums.PaymentEventType;

import java.time.LocalDateTime;

public class PaymentStatusEvent {

    private String orderNo;
    private PaymentEventType eventType;
    private String reason;
    private String operator;
    private LocalDateTime eventTime;

    public static PaymentStatusEvent of(String orderNo, PaymentEventType type, String reason, String operator) {
        PaymentStatusEvent event = new PaymentStatusEvent();
        event.orderNo = orderNo;
        event.eventType = type;
        event.reason = reason;
        event.operator = operator;
        event.eventTime = LocalDateTime.now();
        return event;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public PaymentEventType getEventType() {
        return eventType;
    }

    public String getReason() {
        return reason;
    }

    public String getOperator() {
        return operator;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }
}

