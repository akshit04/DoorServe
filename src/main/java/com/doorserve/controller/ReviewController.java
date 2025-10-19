package com.doorserve.controller;

import com.doorserve.model.Review;
import com.doorserve.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<List<Review>> getPartnerReviews(@PathVariable Long partnerId) {
        List<Review> reviews = reviewService.getReviewsForPartner(partnerId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/partner/{partnerId}/stats")
    public ResponseEntity<Map<String, Object>> getPartnerReviewStats(@PathVariable Long partnerId) {
        Map<String, Object> stats = reviewService.getPartnerReviewStats(partnerId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/service/{serviceId}/partner/{partnerId}")
    public ResponseEntity<List<Review>> getPartnerReviewsForService(
            @PathVariable Long serviceId, 
            @PathVariable Long partnerId) {
        List<Review> reviews = reviewService.getPartnerReviewsForService(serviceId, partnerId);
        return ResponseEntity.ok(reviews);
    }
}