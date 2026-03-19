package com.example.payment.infrastructure.mq;

import com.example.payment.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentOrderChangedMessage {

    private String messageId;
    private String eventType;
    private String orderNo;
    private String merchantNo;
    private String channelCode;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime eventTime;

    public static PaymentOrderChangedMessage of(String messageId,
                                                String eventType,
                                                String orderNo,
                                                String merchantNo,
                                                String channelCode,
                                                BigDecimal amount,
                                                PaymentStatus status,
                                                LocalDateTime eventTime) {
        PaymentOrderChangedMessage msg = new PaymentOrderChangedMessage();
        msg.messageId = messageId;
        msg.eventType = eventType;
        msg.orderNo = orderNo;
        msg.merchantNo = merchantNo;
        msg.channelCode = channelCode;
        msg.amount = amount;
        msg.status = status;
        msg.eventTime = eventTime;
        return msg;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getMerchantNo() {
        return merchantNo;
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

    public LocalDateTime getEventTime() {
        return eventTime;
    }
}

