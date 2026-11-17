package com.example.shop.service;

import com.example.shop.dto.request.UpdateProfileRequest;
import com.example.shop.dto.response.UserResponse;

public interface UserService {
    UserResponse getCurrentUser(String email);

    UserResponse updateProfile(String email, UpdateProfileRequest request);

    void deactivateAccount(String email);
}
