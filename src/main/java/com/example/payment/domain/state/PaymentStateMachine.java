package com.example.payment.domain.state;

import com.example.payment.domain.enums.PaymentEventType;
import com.example.payment.domain.enums.PaymentStatus;

import java.util.EnumMap;
import java.util.Map;

public class PaymentStateMachine {

    private static final Map<PaymentStatus, Map<PaymentEventType, PaymentStatus>> STATE_GRAPH =
            new EnumMap<>(PaymentStatus.class);

    static {
        register(PaymentStatus.CREATED, PaymentEventType.SUBMIT, PaymentStatus.PAYING);
        register(PaymentStatus.CREATED, PaymentEventType.CLOSE, PaymentStatus.CLOSED);

        register(PaymentStatus.PAYING, PaymentEventType.CHANNEL_ACCEPTED, PaymentStatus.PAYING);
        register(PaymentStatus.PAYING, PaymentEventType.CHANNEL_SUCCESS, PaymentStatus.SUCCESS);
        register(PaymentStatus.PAYING, PaymentEventType.CHANNEL_FAIL, PaymentStatus.FAILED);
        register(PaymentStatus.PAYING, PaymentEventType.TIMEOUT, PaymentStatus.CLOSED);

        register(PaymentStatus.SUCCESS, PaymentEventType.REFUND_APPLY, PaymentStatus.REFUNDING);

        register(PaymentStatus.REFUNDING, PaymentEventType.REFUND_SUCCESS, PaymentStatus.REFUNDED);
        register(PaymentStatus.REFUNDING, PaymentEventType.REFUND_FAIL, PaymentStatus.SUCCESS);
    }

    private static void register(PaymentStatus source, PaymentEventType event, PaymentStatus target) {
        STATE_GRAPH.computeIfAbsent(source, k -> new EnumMap<>(PaymentEventType.class))
                .put(event, target);
    }

    public PaymentStatus transit(PaymentStatus source, PaymentEventType event) {
        Map<PaymentEventType, PaymentStatus> eventMap = STATE_GRAPH.get(source);
        if (eventMap == null || !eventMap.containsKey(event)) {
            throw new IllegalStateException("Illegal state transition: " + source + " -> " + event);
        }
        return eventMap.get(event);
    }
}

