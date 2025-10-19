package com.doorserve.service;

import com.doorserve.model.Cart;
import com.doorserve.model.PartnerService;
import com.doorserve.model.User;
import com.doorserve.repository.CartRepository;
import com.doorserve.repository.PartnerServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final PartnerServiceRepository partnerServiceRepository;

    public List<Cart> getUserCart(User user) {
        return cartRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public Cart addToCart(User user, Long partnerServiceId, Integer quantity) {
        PartnerService partnerService = partnerServiceRepository.findById(partnerServiceId)
                .orElseThrow(() -> new RuntimeException("Partner service not found"));

        Optional<Cart> existingCartItem = cartRepository.findByUserAndPartnerServiceId(user, partnerServiceId);
        
        if (existingCartItem.isPresent()) {
            // Update quantity if item already exists
            Cart cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartRepository.save(cartItem);
        } else {
            // Create new cart item
            Cart cartItem = new Cart();
            cartItem.setUser(user);
            cartItem.setPartnerService(partnerService);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(partnerService.getPrice());
            return cartRepository.save(cartItem);
        }
    }

    @Transactional
    public Cart updateCartItem(User user, Long cartItemId, Integer quantity) {
        Cart cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        if (quantity <= 0) {
            cartRepository.delete(cartItem);
            return null;
        }

        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(User user, Long cartItemId) {
        Cart cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        cartRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(User user) {
        cartRepository.deleteByUser(user);
    }

    public int getCartItemCount(User user) {
        return getUserCart(user).stream()
                .mapToInt(Cart::getQuantity)
                .sum();
    }
}