package com.doorserve.controller;

import com.doorserve.dto.AuthResponse;
import com.doorserve.dto.LoginRequest;
import com.doorserve.dto.RegisterRequest;
import com.doorserve.model.User;
import com.doorserve.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("status", "OAuth2 configuration is working");
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(currentUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/callback")
    public Map<String, Object> callback(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) String error) {

        log.info("Auth callback received - token: {}, userType: {}, error: {}",
                token != null ? "present" : "null", userType, error);

        return Map.of(
                "token", token != null ? token : "null",
                "userType", userType != null ? userType : "null",
                "error", error != null ? error : "null");
    }
}