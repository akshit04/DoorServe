package com.doorserve.service;

import com.doorserve.model.Review;
import com.doorserve.model.User;
import com.doorserve.repository.ReviewRepository;
import com.doorserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public List<Review> getReviewsForPartner(Long partnerId) {
        User partner = userRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));
        return reviewRepository.findByPartnerOrderByCreatedAtDesc(partner);
    }

    public Map<String, Object> getPartnerReviewStats(Long partnerId) {
        Double averageRating = reviewRepository.getAverageRatingForPartner(partnerId);
        Long totalReviews = reviewRepository.getTotalReviewsForPartner(partnerId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", averageRating != null ? averageRating : 0.0);
        stats.put("totalReviews", totalReviews != null ? totalReviews : 0L);
        
        // Get rating distribution
        List<Review> allReviews = getReviewsForPartner(partnerId);
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            long count = allReviews.stream()
                    .filter(review -> review.getRating() == rating)
                    .count();
            ratingDistribution.put(rating, count);
        }
        stats.put("ratingDistribution", ratingDistribution);
        
        return stats;
    }

    public List<Review> getPartnerReviewsForService(Long serviceId, Long partnerId) {
        // For now, return all partner reviews. In the future, we could filter by service
        return getReviewsForPartner(partnerId);
    }
}