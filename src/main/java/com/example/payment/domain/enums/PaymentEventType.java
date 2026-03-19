package com.example.payment.domain.enums;

/**
 * 支付订单状态机事件定义。
 */
public enum PaymentEventType {
    SUBMIT,
    CHANNEL_ACCEPTED,
    CHANNEL_SUCCESS,
    CHANNEL_FAIL,
    TIMEOUT,
    CLOSE,
    REFUND_APPLY,
    REFUND_SUCCESS,
    REFUND_FAIL
}

