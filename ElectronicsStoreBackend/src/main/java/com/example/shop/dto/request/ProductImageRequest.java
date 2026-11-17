package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record ProductImageRequest(

        @NotBlank(message = "Image URL is required")
        @Size(max = 255)
        String imageUrl,

        @Size(max = 255)
        String thumbnailUrl,

        @Size(max = 255)
        String altText,

        Boolean isPrimary,

        @PositiveOrZero
        Integer sortOrder) {
}
