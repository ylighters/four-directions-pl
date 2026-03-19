package com.example.payment.application.service;

import com.example.payment.domain.enums.PaymentPlatform;
import com.example.payment.infrastructure.persistence.mapper.PayChannelMapper;
import com.example.payment.infrastructure.persistence.po.PayChannelPO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class ChannelRoutingService {

    private final PayChannelMapper payChannelMapper;
    private final ObjectMapper objectMapper;

    public ChannelRoutingService(PayChannelMapper payChannelMapper, ObjectMapper objectMapper) {
        this.payChannelMapper = payChannelMapper;
        this.objectMapper = objectMapper;
    }

    public ChannelRouteResult resolve(PaymentPlatform platform, String specifiedChannelCode) {
        return resolveCandidates(platform, specifiedChannelCode).stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "支付渠道未配置"));
    }

    public java.util.List<ChannelRouteResult> resolveCandidates(PaymentPlatform platform, String specifiedChannelCode) {
        if (specifiedChannelCode != null && !specifiedChannelCode.isBlank()) {
            PayChannelPO byCode = payChannelMapper.selectActiveByChannelCode(specifiedChannelCode);
            if (byCode == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "支付渠道未配置或未启用: " + specifiedChannelCode);
            }
            return java.util.List.of(toResult(byCode));
        }

        String type = mapChannelType(platform);
        java.util.List<PayChannelPO> channels = payChannelMapper.selectActiveByChannelType(type);
        if (channels.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "支付渠道未配置: " + type);
        }
        return channels.stream().map(this::toResult).toList();
    }

    private String mapChannelType(PaymentPlatform platform) {
        return switch (platform) {
            case WECHAT -> "WECHAT";
            case ALIPAY -> "ALIPAY";
            case ALLINPAY -> "ALLINPAY";
        };
    }

    private Map<String, Object> parseConfig(String json) {
        try {
            if (json == null || json.isBlank()) {
                return new java.util.HashMap<>();
            }
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "支付渠道配置JSON解析失败", ex);
        }
    }

    private ChannelRouteResult toResult(PayChannelPO channel) {
        Map<String, Object> apiConfig = parseConfig(channel.getApiConfig());
        if (!apiConfig.containsKey("mchId") && channel.getMchId() != null) {
            apiConfig.put("mchId", channel.getMchId());
        }
        return new ChannelRouteResult(channel.getChannelCode(), apiConfig);
    }

    public record ChannelRouteResult(String channelCode, Map<String, Object> apiConfig) {
    }
}
