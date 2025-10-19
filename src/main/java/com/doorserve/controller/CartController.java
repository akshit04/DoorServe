package com.doorserve.controller;

import com.doorserve.model.Cart;
import com.doorserve.model.User;
import com.doorserve.service.AuthService;
import com.doorserve.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<Cart>> getCart(Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        List<Cart> cartItems = cartService.getUserCart(currentUser);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        Long serviceId = Long.valueOf(request.get("serviceId").toString());
        Integer quantity = Integer.valueOf(request.getOrDefault("quantity", 1).toString());
        
        Cart cartItem = cartService.addToCart(currentUser, serviceId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<Cart> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        
        Cart updatedItem = cartService.updateCartItem(currentUser, cartItemId, quantity);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        cartService.removeFromCart(currentUser, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        cartService.clearCart(currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCartCount(Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        int count = cartService.getCartItemCount(currentUser);
        return ResponseEntity.ok(Map.of("count", count));
    }
}