package com.example.payment.infrastructure.channel;

import com.example.payment.domain.enums.PaymentPlatform;
import com.example.payment.infrastructure.channel.model.ChannelUnifiedOrderRequest;
import com.example.payment.infrastructure.channel.model.ChannelUnifiedOrderResponse;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WechatChannelClient extends AbstractHttpChannelClient {

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final Map<String, CloseableHttpClient> clientCache = new ConcurrentHashMap<>();

    public WechatChannelClient(ChannelHttpExecutor channelHttpExecutor,
                               ChannelCryptoFacade channelCryptoFacade,
                               com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        super(channelHttpExecutor, channelCryptoFacade);
        this.objectMapper = objectMapper;
    }

    @Override
    public PaymentPlatform supportPlatform() {
        return PaymentPlatform.WECHAT;
    }

    @Override
    public ChannelUnifiedOrderResponse unifiedOrder(ChannelUnifiedOrderRequest request) {
        try {
            Map<String, Object> cfg = request.getChannelConfig();
            String mchId = required(cfg, "mchId");
            String appId = required(cfg, "appId");
            String serialNo = required(cfg, "serialNo");
            String privateKeyPem = required(cfg, "privateKey");
            String apiV3Key = required(cfg, "apiV3Key");
            String gatewayUrl = required(cfg, "gatewayUrl");

            CloseableHttpClient httpClient = clientCache.computeIfAbsent(request.getChannelCode(), k ->
                    buildClient(mchId, serialNo, privateKeyPem, apiV3Key));

            Map<String, Object> amount = Map.of("total", toFen(request.getAmount()), "currency", request.getCurrency());
            Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("appid", appId);
            payload.put("mchid", mchId);
            payload.put("description", request.getSubject());
            payload.put("out_trade_no", request.getOrderNo());
            payload.put("notify_url", request.getNotifyUrl());
            payload.put("amount", amount);

            HttpPost post = new HttpPost(gatewayUrl);
            post.addHeader("Accept", "application/json");
            post.addHeader("Content-Type", "application/json; charset=utf-8");
            post.setEntity(new StringEntity(objectMapper.writeValueAsString(payload), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode < 200 || statusCode >= 300) {
                    throw new ChannelInvokeException("Wechat unified order failed, status=" + statusCode + ", body=" + body);
                }
                com.fasterxml.jackson.databind.JsonNode json = objectMapper.readTree(body);
                String codeUrl = json.path("code_url").asText();
                return ChannelUnifiedOrderResponse.of(
                        "WX-" + request.getOrderNo(),
                        codeUrl,
                        codeUrl,
                        body
                );
            }
        } catch (ChannelInvokeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ChannelInvokeException("Wechat SDK invoke failed", ex);
        }
    }

    private CloseableHttpClient buildClient(String mchId, String serialNo, String privateKeyPem, String apiV3Key) {
        try {
            PrivateKey privateKey = PemUtil.loadPrivateKey(
                    new java.io.ByteArrayInputStream(privateKeyPem.getBytes(StandardCharsets.UTF_8)));

            Verifier verifier = new AutoUpdateCertificatesVerifier(
                    new WechatPay2Credentials(mchId, new PrivateKeySigner(serialNo, privateKey)),
                    apiV3Key.getBytes(StandardCharsets.UTF_8)
            );

            return WechatPayHttpClientBuilder.create()
                    .withMerchant(mchId, serialNo, privateKey)
                    .withValidator(new WechatPay2Validator(verifier))
                    .build();
        } catch (Exception ex) {
            throw new ChannelInvokeException("Build wechat http client failed", ex);
        }
    }

    private String required(Map<String, Object> cfg, String key) {
        Object v = cfg.get(key);
        if (v == null || String.valueOf(v).isBlank()) {
            throw new ChannelInvokeException("Missing wechat config: " + key);
        }
        return String.valueOf(v);
    }

    private int toFen(java.math.BigDecimal yuan) {
        return yuan.multiply(java.math.BigDecimal.valueOf(100)).setScale(0, java.math.RoundingMode.HALF_UP).intValueExact();
    }
}
