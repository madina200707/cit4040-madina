package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record CreateCategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 255)
        String name,

        @Size(max = 1000)
        String description,

        @Size(max = 255)
        String imageUrl,

        @PositiveOrZero
        Integer sortOrder,

        Long parentCategoryId) {
}
