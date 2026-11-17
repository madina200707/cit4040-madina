package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;

public record UpdateReviewRequest(

        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        Integer rating,

        @Size(max = 200)
        String title,

        @Size(max = 5000)
        String comment,

        @Size(max = 2000)
        String pros,

        @Size(max = 2000)
        String cons,

        @Size(max = 10)
        List<@NotBlank String> images) {
}
