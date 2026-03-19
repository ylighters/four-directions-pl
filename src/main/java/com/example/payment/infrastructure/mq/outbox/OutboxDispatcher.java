package com.example.payment.infrastructure.mq.outbox;

import com.example.payment.infrastructure.persistence.po.OutboxMessagePO;
import com.example.payment.infrastructure.persistence.mapper.OutboxMapper;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "payment.mq", name = "enabled", havingValue = "true")
public class OutboxDispatcher {

    private static final Logger log = LoggerFactory.getLogger(OutboxDispatcher.class);

    private final OutboxMapper outboxMapper;
    private final RocketMQTemplate rocketMQTemplate;
    private final int scanBatchSize;
    private final int retrySeconds;

    public OutboxDispatcher(OutboxMapper outboxMapper,
                            RocketMQTemplate rocketMQTemplate,
                            @Value("${payment.mq.outbox-scan-batch-size:100}") int scanBatchSize,
                            @Value("${payment.mq.outbox-retry-seconds:30}") int retrySeconds) {
        this.outboxMapper = outboxMapper;
        this.rocketMQTemplate = rocketMQTemplate;
        this.scanBatchSize = scanBatchSize;
        this.retrySeconds = retrySeconds;
    }

    @Scheduled(fixedDelay = 1000)
    public void dispatch() {
        List<OutboxMessagePO> messages = outboxMapper.scanDispatchable(LocalDateTime.now(), scanBatchSize);
        if (messages.isEmpty()) {
            return;
        }

        for (OutboxMessagePO message : messages) {
            int locked = outboxMapper.markSending(message.getId());
            if (locked <= 0) {
                continue;
            }
            try {
                SendResult result = rocketMQTemplate.syncSend(message.getTopic(), message.getMsgBody(), 3000);
                if (result != null && StringUtils.hasText(result.getMsgId())) {
                    outboxMapper.markSent(message.getId());
                    continue;
                }
                scheduleRetry(message.getId());
            } catch (Exception ex) {
                log.error("Outbox dispatch failed, id={}, topic={}, bizKey={}",
                        message.getId(), message.getTopic(), message.getBizKey(), ex);
                scheduleRetry(message.getId());
            }
        }
    }

    private void scheduleRetry(Long id) {
        outboxMapper.markRetry(id, LocalDateTime.now().plusSeconds(retrySeconds));
    }
}

