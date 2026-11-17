package com.example.shop.exception;

import org.springframework.http.HttpStatus;

public class OrderCancellationException extends ApiException {
    public OrderCancellationException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
