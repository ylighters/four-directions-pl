package com.example.payment.infrastructure.channel.model;

import com.example.payment.domain.enums.PaymentPlatform;

import java.math.BigDecimal;
import java.util.Map;

public class ChannelUnifiedOrderRequest {

    private String orderNo;
    private String merchantOrderNo;
    private String merchantNo;
    private String appId;
    private PaymentPlatform platform;
    private String channelCode;
    private String deviceNo;
    private BigDecimal amount;
    private String currency;
    private String subject;
    private String notifyUrl;
    private String returnUrl;
    private Map<String, Object> channelConfig;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMerchantOrderNo() {
        return merchantOrderNo;
    }

    public void setMerchantOrderNo(String merchantOrderNo) {
        this.merchantOrderNo = merchantOrderNo;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public PaymentPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(PaymentPlatform platform) {
        this.platform = platform;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public Map<String, Object> getChannelConfig() {
        return channelConfig;
    }

    public void setChannelConfig(Map<String, Object> channelConfig) {
        this.channelConfig = channelConfig;
    }
}