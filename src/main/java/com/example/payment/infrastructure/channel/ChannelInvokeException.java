package com.example.payment.infrastructure.channel;

public class ChannelInvokeException extends RuntimeException {

    public ChannelInvokeException(String message) {
        super(message);
    }

    public ChannelInvokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
