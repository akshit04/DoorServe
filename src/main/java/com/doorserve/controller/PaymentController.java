package com.doorserve.controller;

import com.doorserve.dto.*;
import com.doorserve.model.User;
import com.doorserve.service.AuthService;
import com.doorserve.service.PaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthService authService;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(
            @RequestBody PaymentIntentRequest request,
            Authentication authentication) {
        try {
            User currentUser = authService.getCurrentUser(authentication);
            PaymentIntentResponse response = paymentService.createPaymentIntent(request, currentUser.getId());
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            System.err.println("Stripe error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Stripe error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Payment error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Payment error: " + e.getMessage());
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<String> confirmPayment(
            @RequestBody PaymentConfirmationRequest request,
            Authentication authentication) {
        try {
            User currentUser = authService.getCurrentUser(authentication);
            paymentService.confirmPayment(request, currentUser.getId());
            return ResponseEntity.ok("Payment confirmed successfully");
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Payment confirmation failed");
        } catch (Exception e) {
            System.err.println("Payment confirmation error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Payment confirmation failed: " + e.getMessage());
        }
    }


}