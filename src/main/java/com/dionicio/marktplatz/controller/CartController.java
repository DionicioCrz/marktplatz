package com.dionicio.marktplatz.controller;

import com.dionicio.marktplatz.dto.AddToCartRequest;
import com.dionicio.marktplatz.dto.CartResponse;
import com.dionicio.marktplatz.dto.UpdateCartItemRequest;
import com.dionicio.marktplatz.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/api/v1/cart")
    public CartResponse getCart(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        return cartService.getCartResponse(userId);
    }

    @PostMapping("/api/v1/cart/items")
    public CartResponse addItem(Authentication authentication, @RequestBody AddToCartRequest request) {
        String userId = (String) authentication.getPrincipal();
        cartService.addItemToCart(userId, request.productId(), request.quantity());
        return cartService.getCartResponse(userId);
    }

    @PutMapping("/api/v1/cart/items/{id}")
    public CartResponse updateItem(Authentication authentication, @PathVariable Long id, @RequestBody UpdateCartItemRequest request) {
        String userId = (String) authentication.getPrincipal();
        cartService.updateCartItemQuantity(userId, id, request.quantity());
        return cartService.getCartResponse(userId);
    }

    @DeleteMapping("/api/v1/cart/items/{id}")
    public CartResponse removeItem(Authentication authentication, @PathVariable Long id) {
        String userId = (String) authentication.getPrincipal();
        cartService.removeCartItem(userId, id);
        return cartService.getCartResponse(userId);
    }

}
