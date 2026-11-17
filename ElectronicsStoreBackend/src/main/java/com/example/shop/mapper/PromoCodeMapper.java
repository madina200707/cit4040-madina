package com.example.shop.mapper;

import com.example.shop.dto.response.DeliveryOptionResponse;
import com.example.shop.dto.response.PromoCodeResponse;
import com.example.shop.model.entity.Category;
import com.example.shop.model.entity.DeliveryOption;
import com.example.shop.model.entity.Product;
import com.example.shop.model.entity.PromoCode;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PromoCodeMapper {
    public PromoCodeResponse toResponse(PromoCode promoCode) {
        return new PromoCodeResponse(
                promoCode.getId(),
                promoCode.getCode(),
                promoCode.getDescription(),
                promoCode.getDiscountType(),
                promoCode.getDiscountValue(),
                promoCode.getMinOrderAmount(),
                promoCode.getMaxDiscountAmount(),
                promoCode.getMaxUsageCount(),
                promoCode.getCurrentUsageCount(),
                promoCode.getMaxUsagePerUser(),
                promoCode.getValidFrom(),
                promoCode.getValidTo(),
                promoCode.getIsActive(),
                promoCode.getCreatedBy(),
                promoCode.getApplicableCategories().stream()
                        .map(Category::getId).collect(Collectors.toSet()),
                promoCode.getApplicableProducts().stream()
                        .map(Product::getId).collect(Collectors.toSet()));
    }

    public DeliveryOptionResponse toResponse(DeliveryOption option) {
        return new DeliveryOptionResponse(
                option.getId(),
                option.getName(),
                option.getCode(),
                option.getDescription(),
                option.getBasePrice(),
                option.getFreeThreshold(),
                option.getEstimatedDaysMin(),
                option.getEstimatedDaysMax(),
                option.getIsActive());
    }
}
