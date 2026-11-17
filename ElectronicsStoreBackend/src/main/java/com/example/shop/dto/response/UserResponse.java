package com.example.shop.dto.response;

import com.example.shop.model.enums.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
Long id,
        String email,
        String firstName,
        String lastName,
        String phone,
        UserRole role,
        Boolean emailVerified,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt) {
}
