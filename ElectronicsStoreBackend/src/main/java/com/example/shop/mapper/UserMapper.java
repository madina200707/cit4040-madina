package com.example.shop.mapper;

import com.example.shop.dto.response.AddressResponse;
import com.example.shop.dto.response.UserResponse;
import com.example.shop.model.entity.Address;
import com.example.shop.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole(),
                user.getEmailVerified(),
                user.getLastLoginAt(),
                user.getCreatedAt());
    }

    public AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getTitle(),
                address.getCountry(),
                address.getCity(),
                address.getStreet(),
                address.getBuilding(),
                address.getApartment(),
                address.getPostalCode(),
                address.getRecipientName(),
                address.getRecipientPhone(),
                address.getIsDefault(),
                address.getLatitude(),
                address.getLongitude());
    }
}
