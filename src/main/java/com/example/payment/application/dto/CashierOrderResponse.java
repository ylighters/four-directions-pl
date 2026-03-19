package com.example.payment.application.dto;

import com.example.payment.domain.enums.PaymentPlatform;
import com.example.payment.domain.enums.PaymentStatus;

import java.math.BigDecimal;

public class CashierOrderResponse {

    private String orderNo;
    private String merchantOrderNo;
    private PaymentPlatform platform;
    private String channelCode;
    private BigDecimal amount;
    private PaymentStatus status;
    private String payUrl;
    private String qrCodeContent;

    public static CashierOrderResponse of(String orderNo,
                                          String merchantOrderNo,
                                          PaymentPlatform platform,
                                          String channelCode,
                                          BigDecimal amount,
                                          PaymentStatus status,
                                          String payUrl,
                                          String qrCodeContent) {
        CashierOrderResponse response = new CashierOrderResponse();
        response.orderNo = orderNo;
        response.merchantOrderNo = merchantOrderNo;
        response.platform = platform;
        response.channelCode = channelCode;
        response.amount = amount;
        response.status = status;
        response.payUrl = payUrl;
        response.qrCodeContent = qrCodeContent;
        return response;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getMerchantOrderNo() {
        return merchantOrderNo;
    }

    public PaymentPlatform getPlatform() {
        return platform;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public String getQrCodeContent() {
        return qrCodeContent;
    }
}