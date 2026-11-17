package com.example.shop.dto.response;

public record AddressResponse(
Long id,
        String title,
        String country,
        String city,
        String street,
        String building,
        String apartment,
        String postalCode,
        String recipientName,
        String recipientPhone,
        Boolean isDefault,
        Double latitude,
        Double longitude) {
}
