package com.example.shop.dto.request;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record UpdateProductRequest(

        @Size(min = 2, max = 200)
        String name,

        @Size(max = 64)
        String sku,

        @Positive(message = "Base price must be positive")
        BigDecimal basePrice,

        Long categoryId,

        Boolean removeDiscount,

        @Size(max = 500)
        String shortDescription,

        @Size(max = 10000)
        String description,

        @Positive(message = "Discount price must be positive")
        BigDecimal discountPrice,

        LocalDateTime discountStartDate,

        LocalDateTime discountEndDate,

        @PositiveOrZero
        Integer lowStockThreshold,

        @PositiveOrZero
        BigDecimal weight,

        @Size(max = 255)
        String dimensions,

        Boolean isFeatured,

        Boolean isNewArrival,

        @Size(max = 255)
        String metaTitle,

        @Size(max = 1000)
        String metaDescription,

        Long brandId,

        List<@Valid ProductImageRequest> images,

        List<@Valid ProductSpecificationRequest> specifications) {
}
