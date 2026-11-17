package com.example.shop.model.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ShippingAddress {
    private String country;

    private String city;

    private String street;

    private String building;

    private String apartment;

    private String postalCode;

    private String recipientName;

    private String recipientPhone;

    private Double latitude;

    private Double longitude;
}
