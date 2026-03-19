package com.example.payment.infrastructure.channel;

import com.example.payment.domain.enums.PaymentPlatform;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ChannelClientFactory {

    private final Map<PaymentPlatform, ChannelClient> clientMap = new EnumMap<>(PaymentPlatform.class);

    public ChannelClientFactory(List<ChannelClient> clients) {
        for (ChannelClient client : clients) {
            clientMap.put(client.supportPlatform(), client);
        }
    }

    public ChannelClient get(PaymentPlatform platform) {
        ChannelClient client = clientMap.get(platform);
        if (client == null) {
            throw new IllegalStateException("No ChannelClient for platform: " + platform);
        }
        return client;
    }
}