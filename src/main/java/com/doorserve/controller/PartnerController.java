package com.doorserve.controller;

import com.doorserve.model.User;
import com.doorserve.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final AuthService authService;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getPartnerProfile(Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        
        // Create a partner profile response
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", currentUser.getId());
        profile.put("email", currentUser.getEmail());
        profile.put("firstName", currentUser.getFirstName());
        profile.put("lastName", currentUser.getLastName());
        profile.put("phone", currentUser.getPhone());
        profile.put("address", currentUser.getAddress());
        profile.put("userType", currentUser.getUserType());
        profile.put("businessName", currentUser.getFirstName() + " " + currentUser.getLastName() + " Services");
        profile.put("description", "Professional service provider");
        profile.put("rating", 4.5);
        profile.put("totalJobs", 0);
        
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updatePartnerProfile(
            @RequestBody Map<String, Object> profileData,
            Authentication authentication) {
        // For now, just return the updated data
        // This would typically update the user in the database
        return ResponseEntity.ok(profileData);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPartnerStats(
            @RequestParam(defaultValue = "month") String period,
            Authentication authentication) {
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBookings", 0);
        stats.put("completedBookings", 0);
        stats.put("cancelledBookings", 0);
        stats.put("totalRevenue", 0.0);
        stats.put("averageRating", 4.5);
        stats.put("period", period);
        
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/availability")
    public ResponseEntity<Map<String, String>> updateAvailability(
            @RequestBody Map<String, Object> availabilityData,
            Authentication authentication) {
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Availability updated successfully");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getAllPartners() {
        // Admin only endpoint - return empty for now
        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin access required");
        
        return ResponseEntity.ok(response);
    }
}