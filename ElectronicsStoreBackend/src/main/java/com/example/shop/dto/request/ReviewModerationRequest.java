package com.example.shop.dto.request;

import jakarta.validation.constraints.*;
import com.example.shop.model.enums.ReviewStatus;

public record ReviewModerationRequest(

        @NotNull(message = "Status is required")
        ReviewStatus status,

        @Size(max = 2000)
        String adminReply) {
}
