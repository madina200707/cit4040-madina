package com.example.shop.controller.customer;

import com.example.shop.dto.request.AddToCartRequest;
import com.example.shop.dto.request.ApplyPromoCodeRequest;
import com.example.shop.dto.request.MergeCartRequest;
import com.example.shop.dto.request.UpdateCartItemRequest;
import com.example.shop.dto.response.CartResponse;
import com.example.shop.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private static final String SESSION_HEADER = "X-Session-Id";

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse get(Authentication authentication,
                            @RequestHeader(value = SESSION_HEADER, required = false) String sessionId) {
        return cartService.getCart(email(authentication), sessionId);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(Authentication authentication,
                                                @RequestHeader(value = SESSION_HEADER, required = false) String sessionId,
                                                @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addItem(email(authentication), sessionId, request));
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(Authentication authentication,
                                   @RequestHeader(value = SESSION_HEADER, required = false) String sessionId,
                                   @PathVariable Long itemId,
                                   @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(email(authentication), sessionId, itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(Authentication authentication,
                                           @RequestHeader(value = SESSION_HEADER, required = false) String sessionId,
                                           @PathVariable Long itemId) {
        cartService.removeItem(email(authentication), sessionId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(Authentication authentication,
                                      @RequestHeader(value = SESSION_HEADER, required = false) String sessionId) {
        cartService.clear(email(authentication), sessionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/apply-promo")
    public CartResponse applyPromo(Authentication authentication,
                                   @RequestHeader(value = SESSION_HEADER, required = false) String sessionId,
                                   @Valid @RequestBody ApplyPromoCodeRequest request) {
        return cartService.applyPromoCode(email(authentication), sessionId, request);
    }

    @DeleteMapping("/promo")
    public CartResponse removePromo(Authentication authentication,
                                    @RequestHeader(value = SESSION_HEADER, required = false) String sessionId) {
        return cartService.removePromoCode(email(authentication), sessionId);
    }

    @PostMapping("/merge")
    public CartResponse merge(Authentication authentication,
                              @Valid @RequestBody MergeCartRequest request) {
        return cartService.merge(authentication.getName(), request.sessionId());
    }

    private static String email(Authentication authentication) {
        return authentication == null ? null : authentication.getName();
    }
}
