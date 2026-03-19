package com.example.payment.infrastructure.mq;

import com.example.payment.infrastructure.persistence.po.OutboxMessagePO;
import com.example.payment.infrastructure.persistence.mapper.OutboxMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OutboxPaymentEventPublisher implements PaymentEventPublisher {

    private final OutboxMapper outboxMapper;
    private final ObjectMapper objectMapper;
    private final String payOrderTopic;

    public OutboxPaymentEventPublisher(OutboxMapper outboxMapper,
                                       ObjectMapper objectMapper,
                                       @Value("${payment.mq.pay-order-topic}") String payOrderTopic) {
        this.outboxMapper = outboxMapper;
        this.objectMapper = objectMapper;
        this.payOrderTopic = payOrderTopic;
    }

    @Override
    public void publish(PaymentOrderChangedMessage message) {
        OutboxMessagePO messageDO = new OutboxMessagePO();
        messageDO.setMsgId(message.getMessageId());
        messageDO.setTopic(payOrderTopic + ":" + message.getEventType());
        messageDO.setBizKey(message.getOrderNo());
        messageDO.setMsgBody(writeJson(message));
        messageDO.setSendStatus("INIT");
        messageDO.setRetryCount(0);
        messageDO.setNextRetryTime(LocalDateTime.now());
        outboxMapper.insert(messageDO);
    }

    private String writeJson(PaymentOrderChangedMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Serialize outbox message failed", ex);
        }
    }
}

