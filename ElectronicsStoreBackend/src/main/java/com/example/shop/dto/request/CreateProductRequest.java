package com.example.shop.dto.request;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateProductRequest(

        @NotBlank(message = "Product name is required")
        @Size(min = 2, max = 200)
        String name,

        @NotBlank(message = "SKU is required")
        @Size(max = 64)
        String sku,

        @NotNull(message = "Base price is required")
        @Positive(message = "Base price must be positive")
        BigDecimal basePrice,

        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero
        Integer stockQuantity,

        @NotNull(message = "Category is required")
        Long categoryId,

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
