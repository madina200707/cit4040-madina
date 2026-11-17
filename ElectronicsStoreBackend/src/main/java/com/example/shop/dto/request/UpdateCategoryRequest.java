package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record UpdateCategoryRequest(

        @Size(min = 1, max = 255)
        String name,

        @Size(max = 1000)
        String description,

        @Size(max = 255)
        String imageUrl,

        @PositiveOrZero
        Integer sortOrder,

        Long parentCategoryId,

        Boolean moveToRoot,

        Boolean isActive) {
}
