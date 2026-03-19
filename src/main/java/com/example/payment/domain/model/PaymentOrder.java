package com.example.payment.domain.model;

import com.example.payment.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class PaymentOrder {

    private Long id;
    private String orderNo;
    private String merchantNo;
    private String appId;
    private String merchantOrderNo;
    private String payScene;
    private String payWay;
    private String channelCode;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String subject;
    private String returnUrl;
    private String notifyUrl;
    private String channelOrderNo;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentOrder create(String orderNo,
                                      String merchantNo,
                                      String appId,
                                      String merchantOrderNo,
                                      String payScene,
                                      String payWay,
                                      BigDecimal amount,
                                      String subject,
                                      String notifyUrl,
                                      String returnUrl,
                                      String currency) {
        PaymentOrder order = new PaymentOrder();
        order.orderNo = orderNo;
        order.merchantNo = merchantNo;
        order.appId = appId;
        order.merchantOrderNo = merchantOrderNo;
        order.payScene = payScene;
        order.payWay = payWay;
        order.amount = amount;
        order.subject = subject;
        order.notifyUrl = notifyUrl;
        order.returnUrl = returnUrl;
        order.currency = currency;
        order.status = PaymentStatus.CREATED;
        order.version = 0;
        order.createdAt = LocalDateTime.now();
        order.updatedAt = order.createdAt;
        return order;
    }

    public void markStatus(PaymentStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "newStatus cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public String getMerchantOrderNo() {
        return merchantOrderNo;
    }

    public void setMerchantOrderNo(String merchantOrderNo) {
        this.merchantOrderNo = merchantOrderNo;
    }

    public String getPayScene() {
        return payScene;
    }

    public void setPayScene(String payScene) {
        this.payScene = payScene;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
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

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getChannelOrderNo() {
        return channelOrderNo;
    }

    public void setChannelOrderNo(String channelOrderNo) {
        this.channelOrderNo = channelOrderNo;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

