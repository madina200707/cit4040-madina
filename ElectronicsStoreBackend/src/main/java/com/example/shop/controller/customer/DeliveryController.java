package com.example.shop.controller.customer;

import com.example.shop.dto.request.DeliveryCalculationRequest;
import com.example.shop.dto.response.DeliveryCalculationResponse;
import com.example.shop.dto.response.DeliveryOptionResponse;
import com.example.shop.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/methods")
    public List<DeliveryOptionResponse> methods() {
        return deliveryService.getActiveMethods();
    }

    @PostMapping("/calculate")
    public DeliveryCalculationResponse calculate(@Valid @RequestBody DeliveryCalculationRequest request) {
        return deliveryService.calculate(request);
    }
}
