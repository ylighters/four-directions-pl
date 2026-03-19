package com.example.payment.infrastructure.channel;

import com.example.payment.domain.enums.PaymentPlatform;
import com.example.payment.infrastructure.channel.model.ChannelUnifiedOrderRequest;
import com.example.payment.infrastructure.channel.model.ChannelUnifiedOrderResponse;

public interface ChannelClient {

    PaymentPlatform supportPlatform();

    ChannelUnifiedOrderResponse unifiedOrder(ChannelUnifiedOrderRequest request);
}