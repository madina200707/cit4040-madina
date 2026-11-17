package com.example.shop.service;

import com.example.shop.dto.request.PaymentRequest;
import com.example.shop.dto.response.PaymentMethodResponse;
import com.example.shop.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse process(String email, PaymentRequest request);

    PaymentResponse getStatus(String email, Long orderId);

    List<PaymentMethodResponse> getMethods();
}
