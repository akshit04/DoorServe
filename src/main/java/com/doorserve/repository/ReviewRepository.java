package com.doorserve.repository;

import com.doorserve.model.Review;
import com.doorserve.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCustomer(User customer);
    List<Review> findByPartner(User partner);
    List<Review> findByPartnerOrderByCreatedAtDesc(User partner);
    Optional<Review> findByBookingIdAndCustomerId(Long bookingId, Long customerId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.partner.id = :partnerId")
    Double getAverageRatingForPartner(@Param("partnerId") Long partnerId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.partner.id = :partnerId")
    Long getTotalReviewsForPartner(@Param("partnerId") Long partnerId);
}