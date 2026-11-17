package com.example.shop.service.impl;

import com.example.shop.dto.request.ChangePasswordRequest;
import com.example.shop.dto.request.LoginRequest;
import com.example.shop.dto.request.RefreshTokenRequest;
import com.example.shop.dto.request.RegisterRequest;
import com.example.shop.dto.response.AuthResponse;
import com.example.shop.dto.response.UserResponse;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.ConflictException;
import com.example.shop.exception.UnauthorizedException;
import com.example.shop.mapper.UserMapper;
import com.example.shop.model.entity.RefreshToken;
import com.example.shop.model.entity.User;
import com.example.shop.model.enums.UserRole;
import com.example.shop.repository.RefreshTokenRepository;
import com.example.shop.repository.UserRepository;
import com.example.shop.security.jwt.JwtTokenProvider;
import com.example.shop.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final UserLookup userLookup;
    private final Duration refreshTokenTtl;

    public AuthServiceImpl(UserRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider,
                           UserMapper userMapper,
                           UserLookup userLookup,
                           @Value("${jwt.refresh-token-expiration}") Duration refreshTokenTtl) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userMapper = userMapper;
        this.userLookup = userLookup;
        this.refreshTokenTtl = refreshTokenTtl;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("This email is already registered");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPhone(request.phone());
        user.setRole(UserRole.CUSTOMER);
        user.setEnabled(true);
        user.setEmailVerified(false);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password()));
        } catch (DisabledException exception) {
            throw new UnauthorizedException("This account is deactivated");
        } catch (AuthenticationException exception) {
            throw new UnauthorizedException("Invalid email or password");
        }
        User user = userLookup.byEmail(email);
        user.setLastLoginAt(LocalDateTime.now());
        return issueTokens(user);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken stored = findUsableToken(request.refreshToken());
        User user = stored.getUser();
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new UnauthorizedException("This account is deactivated");
        }
        stored.setRevoked(true);
        return issueTokens(user);
    }

    @Override
    public void logout(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token not recognised"));
        stored.setRevoked(true);
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userLookup.byEmail(email);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from the current one");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        revokeAllRefreshTokens(user);
    }

    public void revokeAllRefreshTokens(User user) {
        refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId())
                .forEach(token -> token.setRevoked(true));
    }

    private RefreshToken findUsableToken(String token) {
        RefreshToken stored = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not recognised"));
        if (Boolean.TRUE.equals(stored.getRevoked())) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }
        if (stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token has expired");
        }
        return stored;
    }

    private AuthResponse issueTokens(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(newRandomToken());
        refreshToken.setUser(user);
        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiryDate(LocalDateTime.now().plus(refreshTokenTtl));
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                tokenProvider.generateAccessToken(user),
                refreshToken.getToken(),
                "Bearer",
                tokenProvider.getAccessTokenTtlSeconds(),
                userMapper.toResponse(user));
    }

    private String newRandomToken() {
        byte[] bytes = new byte[64];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
