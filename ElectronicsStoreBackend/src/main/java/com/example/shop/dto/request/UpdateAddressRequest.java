package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record UpdateAddressRequest(

        @Size(max = 100)
        String title,

        @Size(max = 100)
        String country,

        @Size(max = 100)
        String city,

        @Size(max = 255)
        String street,

        @Size(max = 50)
        String building,

        @Size(max = 50)
        String apartment,

        @Size(max = 20)
        String postalCode,

        @Size(max = 200)
        String recipientName,

        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone must contain 7 to 15 digits")
        String recipientPhone,

        Boolean isDefault,

        @DecimalMin("-90.0") @DecimalMax("90.0")
        Double latitude,

        @DecimalMin("-180.0") @DecimalMax("180.0")
        Double longitude) {
}
