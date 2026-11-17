package com.example.shop.controller.customer;

import com.example.shop.dto.response.CartResponse;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.WishlistItemResponse;
import com.example.shop.service.WishlistService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public PageResponse<WishlistItemResponse> list(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "addedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return wishlistService.getWishlist(authentication.getName(), pageable);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<WishlistItemResponse> add(Authentication authentication,
                                                    @PathVariable Long productId) {
        WishlistItemResponse added = wishlistService.add(authentication.getName(), productId);
        return ResponseEntity.created(URI.create("/api/wishlist")).body(added);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(Authentication authentication, @PathVariable Long productId) {
        wishlistService.remove(authentication.getName(), productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/move-to-cart")
    public CartResponse moveToCart(Authentication authentication, @PathVariable Long productId) {
        return wishlistService.moveToCart(authentication.getName(), productId);
    }
}
