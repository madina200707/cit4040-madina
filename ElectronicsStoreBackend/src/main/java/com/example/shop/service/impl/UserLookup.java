package com.example.shop.service.impl;

import com.example.shop.exception.UnauthorizedException;
import com.example.shop.model.entity.User;
import com.example.shop.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserLookup {
    private final UserRepository userRepository;

    public UserLookup(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User byEmail(String email) {
        if (email == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("User account no longer exists"));
    }
}
