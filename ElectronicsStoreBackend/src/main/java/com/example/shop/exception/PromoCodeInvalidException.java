package com.example.shop.exception;

import org.springframework.http.HttpStatus;

public class PromoCodeInvalidException extends ApiException {
    public PromoCodeInvalidException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
