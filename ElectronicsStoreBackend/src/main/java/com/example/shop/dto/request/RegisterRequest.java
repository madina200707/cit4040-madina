package com.example.shop.dto.request;

import jakarta.validation.constraints.*;
import com.example.shop.validator.UniqueEmail;
import com.example.shop.validator.ValidPassword;

public record RegisterRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email is not valid")
        @Size(max = 255)
        @UniqueEmail
        String email,

        @NotBlank(message = "Password is required")
        @ValidPassword
        String password,

        @NotBlank(message = "First name is required")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100)
        String lastName,

        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone must contain 7 to 15 digits")
        String phone) {
}
