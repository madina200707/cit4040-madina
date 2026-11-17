package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record MergeCartRequest(

        @NotBlank(message = "Guest session id is required")
        @Size(max = 255)
        String sessionId) {
}
