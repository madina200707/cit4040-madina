package com.example.shop.exception;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

public abstract class ApiException extends RuntimeException {
    private final HttpStatus status;

    protected ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, String> getDetails() {
        return Collections.emptyMap();
    }
}
