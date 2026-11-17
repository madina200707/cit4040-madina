package com.example.shop.controller.customer;

import com.example.shop.dto.request.PaymentRequest;
import com.example.shop.dto.response.PaymentMethodResponse;
import com.example.shop.dto.response.PaymentResponse;
import com.example.shop.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public PaymentResponse process(Authentication authentication,
                                   @Valid @RequestBody PaymentRequest request) {
        return paymentService.process(authentication.getName(), request);
    }

    @GetMapping("/{orderId}/status")
    public PaymentResponse status(Authentication authentication, @PathVariable Long orderId) {
        return paymentService.getStatus(authentication.getName(), orderId);
    }

    @GetMapping("/methods")
    public List<PaymentMethodResponse> methods() {
        return paymentService.getMethods();
    }
}
