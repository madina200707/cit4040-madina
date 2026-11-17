package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record RefreshTokenRequest(

        @NotBlank(message = "Refresh token is required")
        String refreshToken) {
}
