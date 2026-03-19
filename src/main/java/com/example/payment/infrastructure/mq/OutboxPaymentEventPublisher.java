package com.example.payment.infrastructure.mq;

import com.example.payment.infrastructure.persistence.po.OutboxMessagePO;
import com.example.payment.infrastructure.persistence.mapper.OutboxMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 基于 Outbox 表的事件发布实现。
 * 先落库，再由扫描任务异步投递到 MQ，保证本地事务一致性。
 */
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
        // 先写 Outbox，确保订单事务提交后消息不会丢失。
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

    /**
     * 消息体序列化为 JSON。
     */
    private String writeJson(PaymentOrderChangedMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Serialize outbox message failed", ex);
        }
    }
}

