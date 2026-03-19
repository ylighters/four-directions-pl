package com.example.payment.infrastructure.channel;

import com.example.payment.infrastructure.channel.model.ChannelUnifiedOrderRequest;
import com.example.payment.infrastructure.channel.model.ChannelUnifiedOrderResponse;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHttpChannelClient implements ChannelClient {

    private final ChannelHttpExecutor channelHttpExecutor;
    private final ChannelCryptoFacade channelCryptoFacade;

    protected AbstractHttpChannelClient(ChannelHttpExecutor channelHttpExecutor,
                                        ChannelCryptoFacade channelCryptoFacade) {
        this.channelHttpExecutor = channelHttpExecutor;
        this.channelCryptoFacade = channelCryptoFacade;
    }

    protected ChannelUnifiedOrderResponse invokeGateway(ChannelUnifiedOrderRequest request,
                                                        String gatewayUrl,
                                                        Map<String, Object> businessPayload,
                                                        String fallbackPrefix) {
        if (gatewayUrl == null || gatewayUrl.isBlank()) {
            return ChannelUnifiedOrderResponse.of(
                    fallbackPrefix + "-" + request.getOrderNo(),
                    "https://cashier.mock/" + fallbackPrefix.toLowerCase() + "/" + request.getOrderNo(),
                    "pay://" + fallbackPrefix.toLowerCase() + "/" + request.getOrderNo(),
                    "gatewayUrl missing, fallback response"
            );
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("orderNo", request.getOrderNo());
        payload.put("merchantOrderNo", request.getMerchantOrderNo());
        payload.put("amount", request.getAmount());
        payload.put("currency", request.getCurrency());
        payload.put("subject", request.getSubject());
        payload.put("notifyUrl", request.getNotifyUrl());
        payload.put("returnUrl", request.getReturnUrl());
        payload.put("channelCode", request.getChannelCode());
        payload.put("deviceNo", request.getDeviceNo());
        payload.put("biz", businessPayload);

        Map<String, String> headers = buildSignatureHeaders(payload, request.getChannelConfig());
        String body = channelHttpExecutor.postJsonWithRetry(request.getChannelCode(), gatewayUrl, headers, payload);
        verifyResponseOrThrow(body, headers, request.getChannelConfig());

        return ChannelUnifiedOrderResponse.of(
                fallbackPrefix + "-" + request.getOrderNo(),
                "https://cashier.gateway/redirect/" + request.getOrderNo(),
                "pay://" + fallbackPrefix.toLowerCase() + "/" + request.getOrderNo(),
                body
        );
    }

    protected ChannelCryptoFacade crypto() {
        return channelCryptoFacade;
    }

    protected Map<String, String> buildSignatureHeaders(Map<String, Object> payload, Map<String, Object> cfg) {
        return new HashMap<>();
    }

    protected void verifyResponseOrThrow(String responseBody, Map<String, String> headers, Map<String, Object> cfg) {
    }
}
