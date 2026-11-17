package com.example.shop.service;

import com.example.shop.dto.request.CreateDeliveryOptionRequest;
import com.example.shop.dto.request.DeliveryCalculationRequest;
import com.example.shop.dto.request.UpdateDeliveryOptionRequest;
import com.example.shop.dto.response.DeliveryCalculationResponse;
import com.example.shop.dto.response.DeliveryOptionResponse;
import com.example.shop.model.entity.DeliveryOption;
import com.example.shop.model.enums.DeliveryMethod;

import java.util.List;

public interface DeliveryService {
    List<DeliveryOptionResponse> getActiveMethods();

    DeliveryCalculationResponse calculate(DeliveryCalculationRequest request);

    DeliveryOption getActiveOption(DeliveryMethod method);

    List<DeliveryOptionResponse> getAll();

    DeliveryOptionResponse create(CreateDeliveryOptionRequest request);

    DeliveryOptionResponse update(Long id, UpdateDeliveryOptionRequest request);

    void deactivate(Long id);
}
