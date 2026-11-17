package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record CreateBrandRequest(

        @NotBlank(message = "Brand name is required")
        @Size(max = 100)
        String name,

        @Size(max = 1000)
        String description,

        @Size(max = 255)
        String logoUrl,

        @Size(max = 255)
        String websiteUrl,

        @Size(max = 100)
        String country) {
}
