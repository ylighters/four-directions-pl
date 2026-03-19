package com.example.payment.infrastructure.channel;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.example.payment.domain.enums.PaymentPlatform;
import com.example.payment.infrastructure.channel.model.ChannelUnifiedOrderRequest;
import com.example.payment.infrastructure.channel.model.ChannelUnifiedOrderResponse;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlipayChannelClient extends AbstractHttpChannelClient {

    private final Map<String, AlipayClient> clientCache = new ConcurrentHashMap<>();

    public AlipayChannelClient(ChannelHttpExecutor channelHttpExecutor, ChannelCryptoFacade channelCryptoFacade) {
        super(channelHttpExecutor, channelCryptoFacade);
    }

    @Override
    public PaymentPlatform supportPlatform() {
        return PaymentPlatform.ALIPAY;
    }

    @Override
    public ChannelUnifiedOrderResponse unifiedOrder(ChannelUnifiedOrderRequest request) {
        try {
            Map<String, Object> cfg = request.getChannelConfig();
            String gatewayUrl = required(cfg, "gatewayUrl");
            String appId = required(cfg, "appId");
            String privateKey = normalizePrivateKey(required(cfg, "privateKey"));
            String alipayPublicKey = normalizePublicKey(required(cfg, "alipayPublicKey"));
            String signType = String.valueOf(cfg.getOrDefault("signType", "RSA2"));

            String clientKey = request.getChannelCode() + "#" + appId;
            AlipayClient client = clientCache.computeIfAbsent(
                    clientKey,
                    k -> buildClient(gatewayUrl, appId, privateKey, alipayPublicKey, signType)
            );

            AlipayTradePrecreateRequest precreateRequest = new AlipayTradePrecreateRequest();
            AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
            model.setOutTradeNo(request.getOrderNo());
            model.setTotalAmount(request.getAmount().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
            model.setSubject(request.getSubject());
            model.setStoreId(request.getDeviceNo());
            precreateRequest.setBizModel(model);
            precreateRequest.setNotifyUrl(request.getNotifyUrl());

            AlipayTradePrecreateResponse response = client.execute(precreateRequest);
            if (response == null || !response.isSuccess()) {
                throw new ChannelInvokeException("Alipay precreate failed: " + (response == null ? "null" : response.getSubMsg()));
            }

            String qrCode = response.getQrCode();
            return ChannelUnifiedOrderResponse.of(
                    "ALI-" + request.getOrderNo(),
                    qrCode,
                    qrCode,
                    response.getBody()
            );
        } catch (ChannelInvokeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ChannelInvokeException("Alipay SDK invoke failed", ex);
        }
    }

    private AlipayClient buildClient(String gatewayUrl,
                                     String appId,
                                     String privateKey,
                                     String alipayPublicKey,
                                     String signType) {
        validateAlipayKeyPair(privateKey, alipayPublicKey);
        AlipayConfig config = new AlipayConfig();
        config.setServerUrl(gatewayUrl);
        config.setAppId(appId);
        config.setPrivateKey(privateKey);
        config.setAlipayPublicKey(alipayPublicKey);
        config.setFormat("json");
        config.setCharset("UTF-8");
        config.setSignType(signType);
        try {
            return new DefaultAlipayClient(config);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String required(Map<String, Object> cfg, String key) {
        Object v = cfg.get(key);
        if (v == null || String.valueOf(v).isBlank()) {
            throw new ChannelInvokeException("Missing alipay config: " + key);
        }
        return String.valueOf(v);
    }

    private String normalizePrivateKey(String key) {
        return normalizePemContent(key, true);
    }

    private String normalizePublicKey(String key) {
        return normalizePemContent(key, false);
    }

    private String normalizePemContent(String raw, boolean privateKey) {
        String normalized = raw
                .replace("\\n", "\n")
                .replace("\r", "")
                .trim();
        normalized = normalized
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\n", "")
                .replace(" ", "")
                .trim();
        if (normalized.isEmpty()) {
            throw new ChannelInvokeException(privateKey
                    ? "Alipay privateKey is empty after normalize"
                    : "Alipay alipayPublicKey is empty after normalize");
        }
        return normalized;
    }

    private void validateAlipayKeyPair(String privateKey, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] privateBytes = Base64.getDecoder().decode(privateKey);
            keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
            byte[] publicBytes = Base64.getDecoder().decode(publicKey);
            keyFactory.generatePublic(new X509EncodedKeySpec(publicBytes));
        } catch (Exception ex) {
            throw new ChannelInvokeException(
                    "\u652f\u4ed8\u5b9d\u5bc6\u94a5\u683c\u5f0f\u9519\u8bef\uff0c\u8bf7\u914d\u7f6ePKCS8\u79c1\u94a5\u548c\u652f\u4ed8\u5b9d\u516c\u94a5",
                    ex
            );
        }
    }
}
