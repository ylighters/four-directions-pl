package com.example.payment.interfaces.rest.system.dto;

import java.math.BigDecimal;

public class PayChannelView {

    private Long id;
    private String channelCode;
    private String channelName;
    private String channelType;
    private String mchId;
    private String apiConfig;
    private BigDecimal feeRate;
    private Integer status;

    public static PayChannelView of(Long id,
                                    String channelCode,
                                    String channelName,
                                    String channelType,
                                    String mchId,
                                    String apiConfig,
                                    BigDecimal feeRate,
                                    Integer status) {
        PayChannelView view = new PayChannelView();
        view.id = id;
        view.channelCode = channelCode;
        view.channelName = channelName;
        view.channelType = channelType;
        view.mchId = mchId;
        view.apiConfig = apiConfig;
        view.feeRate = feeRate;
        view.status = status;
        return view;
    }

    public Long getId() {
        return id;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelType() {
        return channelType;
    }

    public String getMchId() {
        return mchId;
    }

    public String getApiConfig() {
        return apiConfig;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public Integer getStatus() {
        return status;
    }
}