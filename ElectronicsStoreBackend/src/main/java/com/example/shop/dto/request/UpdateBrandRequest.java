package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record UpdateBrandRequest(

        @Size(min = 1, max = 100)
        String name,

        @Size(max = 1000)
        String description,

        @Size(max = 255)
        String logoUrl,

        @Size(max = 255)
        String websiteUrl,

        @Size(max = 100)
        String country,

        Boolean isActive) {
}
