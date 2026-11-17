package com.example.shop.controller.customer;

import com.example.shop.dto.request.ChangePasswordRequest;
import com.example.shop.dto.request.CreateAddressRequest;
import com.example.shop.dto.request.UpdateAddressRequest;
import com.example.shop.dto.request.UpdateProfileRequest;
import com.example.shop.dto.response.AddressResponse;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.UserResponse;
import com.example.shop.service.AddressService;
import com.example.shop.service.AuthService;
import com.example.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final AddressService addressService;

    public UserController(UserService userService, AuthService authService,
                          AddressService addressService) {
        this.userService = userService;
        this.authService = authService;
        this.addressService = addressService;
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return userService.getCurrentUser(authentication.getName());
    }

    @PutMapping("/me")
    public UserResponse updateMe(Authentication authentication,
                                 @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(authentication.getName(), request);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(Authentication authentication,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(Authentication authentication) {
        userService.deactivateAccount(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/addresses")
    public PageResponse<AddressResponse> addresses(Authentication authentication,
                                                   @PageableDefault(size = 20) Pageable pageable) {
        return addressService.getMyAddresses(authentication.getName(), pageable);
    }

    @GetMapping("/me/addresses/{id}")
    public AddressResponse address(Authentication authentication, @PathVariable Long id) {
        return addressService.getMyAddress(authentication.getName(), id);
    }

    @PostMapping("/me/addresses")
    public ResponseEntity<AddressResponse> createAddress(Authentication authentication,
                                                         @Valid @RequestBody CreateAddressRequest request) {
        AddressResponse created = addressService.create(authentication.getName(), request);
        return ResponseEntity.created(URI.create("/api/users/me/addresses/" + created.id())).body(created);
    }

    @PutMapping("/me/addresses/{id}")
    public AddressResponse updateAddress(Authentication authentication, @PathVariable Long id,
                                         @Valid @RequestBody UpdateAddressRequest request) {
        return addressService.update(authentication.getName(), id, request);
    }

    @DeleteMapping("/me/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(Authentication authentication, @PathVariable Long id) {
        addressService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/addresses/{id}/default")
    public AddressResponse setDefault(Authentication authentication, @PathVariable Long id) {
        return addressService.setDefault(authentication.getName(), id);
    }
}
