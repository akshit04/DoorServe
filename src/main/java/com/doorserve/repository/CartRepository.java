package com.doorserve.repository;

import com.doorserve.model.Cart;
import com.doorserve.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
    List<Cart> findByUserOrderByCreatedAtDesc(User user);
    Optional<Cart> findByUserAndPartnerServiceId(User user, Long partnerServiceId);
    void deleteByUser(User user);
}