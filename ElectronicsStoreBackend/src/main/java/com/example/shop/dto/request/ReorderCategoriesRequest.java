package com.example.shop.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record ReorderCategoriesRequest(
        @NotEmpty(message = "At least one category is required")
        List<@Valid Item> items) {
    public record Item(
            @NotNull(message = "Category id is required")
            Long id,

            @NotNull(message = "Sort order is required")
            @PositiveOrZero
            Integer sortOrder) {
    }
}
