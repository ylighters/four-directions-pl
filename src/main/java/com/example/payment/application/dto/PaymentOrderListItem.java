package com.example.payment.application.dto;

import com.example.payment.domain.enums.PaymentPlatform;
import com.example.payment.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表项响应对象。
 */
public class PaymentOrderListItem {

    private String orderNo;
    private String merchantOrderNo;
    private String merchantNo;
    private String appId;
    private PaymentPlatform platform;
    private String channelCode;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;

    /**
     * 工厂方法，统一构造列表项。
     */
    public static PaymentOrderListItem of(String orderNo,
                                          String merchantOrderNo,
                                          String merchantNo,
                                          String appId,
                                          PaymentPlatform platform,
                                          String channelCode,
                                          BigDecimal amount,
                                          PaymentStatus status,
                                          LocalDateTime createdAt) {
        PaymentOrderListItem item = new PaymentOrderListItem();
        item.orderNo = orderNo;
        item.merchantOrderNo = merchantOrderNo;
        item.merchantNo = merchantNo;
        item.appId = appId;
        item.platform = platform;
        item.channelCode = channelCode;
        item.amount = amount;
        item.status = status;
        item.createdAt = createdAt;
        return item;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getMerchantOrderNo() {
        return merchantOrderNo;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public String getAppId() {
        return appId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
