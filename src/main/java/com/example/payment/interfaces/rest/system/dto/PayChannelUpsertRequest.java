package com.example.payment.interfaces.rest.system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PayChannelUpsertRequest {

    @NotBlank
    private String channelCode;

    @NotBlank
    private String channelName;

    @NotBlank
    private String channelType;

    @NotBlank
    private String mchId;

    @NotBlank
    private String apiConfig;

    @NotNull
    @DecimalMin("0")
    private BigDecimal feeRate;

    @NotNull
    private Integer status;

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getApiConfig() {
        return apiConfig;
    }

    public void setApiConfig(String apiConfig) {
        this.apiConfig = apiConfig;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}