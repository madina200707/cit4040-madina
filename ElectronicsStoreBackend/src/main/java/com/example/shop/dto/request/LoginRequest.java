package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record LoginRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email is not valid")
        String email,

        @NotBlank(message = "Password is required")
        String password) {
}
