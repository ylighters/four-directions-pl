package com.example.payment.infrastructure.channel;

import com.example.payment.domain.enums.PaymentPlatform;
import com.example.payment.infrastructure.channel.model.ChannelUnifiedOrderRequest;
import com.example.payment.infrastructure.channel.model.ChannelUnifiedOrderResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AllinpayChannelClient extends AbstractHttpChannelClient {

    public AllinpayChannelClient(ChannelHttpExecutor channelHttpExecutor, ChannelCryptoFacade channelCryptoFacade) {
        super(channelHttpExecutor, channelCryptoFacade);
    }

    @Override
    public PaymentPlatform supportPlatform() {
        return PaymentPlatform.ALLINPAY;
    }

    @Override
    public ChannelUnifiedOrderResponse unifiedOrder(ChannelUnifiedOrderRequest request) {
        Map<String, Object> cfg = request.getChannelConfig();
        Map<String, Object> biz = new HashMap<>();
        biz.put("orgId", cfg.get("orgId"));
        biz.put("cusid", cfg.get("cusid"));
        biz.put("appId", cfg.get("appId"));
        biz.put("key", cfg.get("key"));

        String gatewayUrl = asString(cfg.get("gatewayUrl"));
        return invokeGateway(request, gatewayUrl, biz, "ALLINPAY");
    }

    @Override
    protected Map<String, String> buildSignatureHeaders(Map<String, Object> payload, Map<String, Object> cfg) {
        String sign = crypto().signAllinpay(payload, cfg);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Allinpay-Sign", sign);
        return headers;
    }

    @Override
    protected void verifyResponseOrThrow(String responseBody, Map<String, String> headers, Map<String, Object> cfg) {
        boolean ok = crypto().verifyAllinpay(responseBody, headers.get("X-Allinpay-Sign"), cfg);
        if (!ok) {
            throw new ChannelInvokeException("Allinpay response signature verify failed");
        }
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
