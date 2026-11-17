package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record UpdateOrderNoteRequest(

        @NotBlank(message = "Note is required")
        @Size(max = 2000)
        String adminNote) {
}
