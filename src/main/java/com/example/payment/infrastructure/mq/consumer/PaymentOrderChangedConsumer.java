package com.example.payment.infrastructure.mq.consumer;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "payment.mq", name = "enabled", havingValue = "true")
@RocketMQMessageListener(
        topic = "${payment.mq.pay-order-topic}",
        consumerGroup = "payment-platform-order-changed-consumer",
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING
)
public class PaymentOrderChangedConsumer implements RocketMQListener<String> {

    private static final Logger log = LoggerFactory.getLogger(PaymentOrderChangedConsumer.class);

    @Override
    public void onMessage(String message) {
        log.info("Receive payment changed event: {}", message);
    }
}
