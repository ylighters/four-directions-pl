package com.example.payment.infrastructure.channel.model;

public class ChannelUnifiedOrderResponse {

    private String channelOrderNo;
    private String payUrl;
    private String qrCodeContent;
    private String rawResponse;

    public static ChannelUnifiedOrderResponse of(String channelOrderNo,
                                                 String payUrl,
                                                 String qrCodeContent,
                                                 String rawResponse) {
        ChannelUnifiedOrderResponse response = new ChannelUnifiedOrderResponse();
        response.channelOrderNo = channelOrderNo;
        response.payUrl = payUrl;
        response.qrCodeContent = qrCodeContent;
        response.rawResponse = rawResponse;
        return response;
    }

    public String getChannelOrderNo() {
        return channelOrderNo;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public String getQrCodeContent() {
        return qrCodeContent;
    }

    public String getRawResponse() {
        return rawResponse;
    }
}