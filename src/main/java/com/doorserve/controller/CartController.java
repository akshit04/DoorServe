package com.doorserve.controller;

import com.doorserve.dto.CartDTO;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<CartDTO>> getCart(Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        List<Cart> cartItems = cartService.getUserCart(currentUser);
        
        List<CartDTO> cartDTOs = cartItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(cartDTOs);
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        Long partnerServiceId = Long.valueOf(request.get("partnerServiceId").toString());
        Integer quantity = Integer.valueOf(request.getOrDefault("quantity", 1).toString());
        
        Cart cartItem = cartService.addToCart(currentUser, partnerServiceId, quantity);
        CartDTO cartDTO = convertToDTO(cartItem);
        return ResponseEntity.ok(cartDTO);
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        
        Cart updatedItem = cartService.updateCartItem(currentUser, cartItemId, quantity);
        if (updatedItem == null) {
            return ResponseEntity.noContent().build();
        }
        CartDTO cartDTO = convertToDTO(updatedItem);
        return ResponseEntity.ok(cartDTO);
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

    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setQuantity(cart.getQuantity());
        dto.setPrice(cart.getPrice());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());
        
        // Set title and duration from PartnerService
        dto.setTitle(cart.getPartnerService().getTitle());
        dto.setDuration(cart.getPartnerService().getDuration());
        
        // Create ServiceDTO from ServicesCatalog
        CartDTO.ServiceDTO serviceDTO = new CartDTO.ServiceDTO();
        serviceDTO.setId(cart.getPartnerService().getServiceCatalog().getId());
        serviceDTO.setName(cart.getPartnerService().getServiceCatalog().getName());
        serviceDTO.setDescription(cart.getPartnerService().getServiceCatalog().getDescription());
        serviceDTO.setCategory(cart.getPartnerService().getServiceCatalog().getCategory());
        serviceDTO.setImageUrl(cart.getPartnerService().getServiceCatalog().getImageUrl());
        dto.setService(serviceDTO);
        
        // Create ProviderDTO from Partner
        CartDTO.ProviderDTO providerDTO = new CartDTO.ProviderDTO();
        providerDTO.setId(cart.getPartnerService().getPartner().getId());
        providerDTO.setName(cart.getPartnerService().getPartner().getFirstName() + " " + 
                           cart.getPartnerService().getPartner().getLastName());
        providerDTO.setRating(cart.getPartnerService().getRating());
        providerDTO.setExperienceYears(cart.getPartnerService().getExperienceYears());
        dto.setProvider(providerDTO);
        
        return dto;
    }
}