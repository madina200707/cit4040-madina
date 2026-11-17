package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record ProductSpecificationRequest(

        @NotBlank(message = "Specification name is required")
        @Size(max = 255)
        String name,

        @NotBlank(message = "Specification value is required")
        @Size(max = 1000)
        String value,

        @Size(max = 255)
        String groupName,

        @PositiveOrZero
        Integer sortOrder) {
}
