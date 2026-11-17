package com.example.shop.service;

public record GatewayResult(boolean success, String errorCode, String errorMessage) {
    public static GatewayResult ok() {
        return new GatewayResult(true, null, null);
    }

    public static GatewayResult declined(String errorCode, String errorMessage) {
        return new GatewayResult(false, errorCode, errorMessage);
    }
}
