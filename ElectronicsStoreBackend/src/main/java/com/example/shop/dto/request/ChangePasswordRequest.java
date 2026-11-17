package com.example.shop.dto.request;

import jakarta.validation.constraints.*;
import com.example.shop.validator.ValidPassword;

public record ChangePasswordRequest(

        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @ValidPassword
        String newPassword) {
}
