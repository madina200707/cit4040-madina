package com.example.shop.exception;

import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentFailedException extends ApiException {
    private final String errorCode;
    private final Long paymentId;

    public PaymentFailedException(String message, String errorCode, Long paymentId) {
        super(HttpStatus.PAYMENT_REQUIRED, message);
        this.errorCode = errorCode;
        this.paymentId = paymentId;
    }

    @Override
    public Map<String, String> getDetails() {
        Map<String, String> details = new LinkedHashMap<>();
        details.put("errorCode", errorCode);
        details.put("paymentId", String.valueOf(paymentId));
        details.put("paymentStatus", "FAILED");
        return details;
    }
}
