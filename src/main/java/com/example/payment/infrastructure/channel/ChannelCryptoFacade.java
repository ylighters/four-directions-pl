package com.example.payment.infrastructure.channel;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@Component
public class ChannelCryptoFacade {

    public String signWechatV3(Map<String, Object> payload, Map<String, Object> cfg) {
        return "WECHAT-SIGN-" + digest(payload.toString() + cfg.toString());
    }

    public String signAlipay(Map<String, Object> payload, Map<String, Object> cfg) {
        return "ALIPAY-SIGN-" + digest(payload.toString() + cfg.toString());
    }

    public String signAllinpay(Map<String, Object> payload, Map<String, Object> cfg) {
        return "ALLINPAY-SIGN-" + digest(payload.toString() + cfg.toString());
    }

    public boolean verifyWechatV3(String responseBody, String signature, Map<String, Object> cfg) {
        return responseBody != null && signature != null;
    }

    public boolean verifyAlipay(String responseBody, String sign, Map<String, Object> cfg) {
        return responseBody != null && sign != null;
    }

    public boolean verifyAllinpay(String responseBody, String sign, Map<String, Object> cfg) {
        return responseBody != null && sign != null;
    }

    private String digest(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Digest failed", ex);
        }
    }
}
