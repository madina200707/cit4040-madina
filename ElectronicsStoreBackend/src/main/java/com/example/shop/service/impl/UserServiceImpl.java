package com.example.shop.service.impl;

import com.example.shop.dto.request.UpdateProfileRequest;
import com.example.shop.dto.response.UserResponse;
import com.example.shop.mapper.UserMapper;
import com.example.shop.model.entity.User;
import com.example.shop.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserLookup userLookup;
    private final UserMapper userMapper;
    private final AuthServiceImpl authService;

    public UserServiceImpl(UserLookup userLookup, UserMapper userMapper,
                           AuthServiceImpl authService) {
        this.userLookup = userLookup;
        this.userMapper = userMapper;
        this.authService = authService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        return userMapper.toResponse(userLookup.byEmail(email));
    }

    @Override
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userLookup.byEmail(email);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPhone(request.phone());
        return userMapper.toResponse(user);
    }

    @Override
    public void deactivateAccount(String email) {
        User user = userLookup.byEmail(email);
        user.setEnabled(false);
        authService.revokeAllRefreshTokens(user);
    }
}
