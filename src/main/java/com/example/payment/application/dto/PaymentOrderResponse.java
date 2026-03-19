package com.example.payment.application.dto;

import com.example.payment.domain.enums.PaymentStatus;

/**
 * 下单结果响应。
 */
public class PaymentOrderResponse {

    private String orderNo;
    private String channelCode;
    private PaymentStatus status;

    /**
     * 工厂方法，统一构造响应。
     */
    public static PaymentOrderResponse of(String orderNo, String channelCode, PaymentStatus status) {
        PaymentOrderResponse resp = new PaymentOrderResponse();
        resp.orderNo = orderNo;
        resp.channelCode = channelCode;
        resp.status = status;
        return resp;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}

