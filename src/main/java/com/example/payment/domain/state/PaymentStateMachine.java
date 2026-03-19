package com.example.payment.domain.state;

import com.example.payment.domain.enums.PaymentEventType;
import com.example.payment.domain.enums.PaymentStatus;

import java.util.EnumMap;
import java.util.Map;

/**
 * 支付状态机。
 * 维护订单状态与事件之间的合法迁移关系。
 */
public class PaymentStateMachine {

    private static final Map<PaymentStatus, Map<PaymentEventType, PaymentStatus>> STATE_GRAPH =
            new EnumMap<>(PaymentStatus.class);

    static {
        // 新建订单可提交到支付中，也可直接关闭。
        register(PaymentStatus.CREATED, PaymentEventType.SUBMIT, PaymentStatus.PAYING);
        register(PaymentStatus.CREATED, PaymentEventType.CLOSE, PaymentStatus.CLOSED);

        // 支付中阶段的渠道回调与超时处理。
        register(PaymentStatus.PAYING, PaymentEventType.CHANNEL_ACCEPTED, PaymentStatus.PAYING);
        register(PaymentStatus.PAYING, PaymentEventType.CHANNEL_SUCCESS, PaymentStatus.SUCCESS);
        register(PaymentStatus.PAYING, PaymentEventType.CHANNEL_FAIL, PaymentStatus.FAILED);
        register(PaymentStatus.PAYING, PaymentEventType.TIMEOUT, PaymentStatus.CLOSED);

        // 成功后可进入退款流程。
        register(PaymentStatus.SUCCESS, PaymentEventType.REFUND_APPLY, PaymentStatus.REFUNDING);

        // 退款结果回写。
        register(PaymentStatus.REFUNDING, PaymentEventType.REFUND_SUCCESS, PaymentStatus.REFUNDED);
        register(PaymentStatus.REFUNDING, PaymentEventType.REFUND_FAIL, PaymentStatus.SUCCESS);
    }

    private static void register(PaymentStatus source, PaymentEventType event, PaymentStatus target) {
        STATE_GRAPH.computeIfAbsent(source, k -> new EnumMap<>(PaymentEventType.class))
                .put(event, target);
    }

    /**
     * 根据当前状态与事件计算目标状态，不允许非法迁移。
     */
    public PaymentStatus transit(PaymentStatus source, PaymentEventType event) {
        Map<PaymentEventType, PaymentStatus> eventMap = STATE_GRAPH.get(source);
        if (eventMap == null || !eventMap.containsKey(event)) {
            throw new IllegalStateException("Illegal state transition: " + source + " -> " + event);
        }
        return eventMap.get(event);
    }
}

