package com.doorserve.repository;

import com.doorserve.model.Booking;
import com.doorserve.model.BookingStatus;
import com.doorserve.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer(User customer);
    List<Booking> findByCustomerAndStatus(User customer, BookingStatus status);
    List<Booking> findByPartner(User partner);
    List<Booking> findByPartnerAndStatus(User partner, BookingStatus status);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByPartnerIdAndBookingDateAndStatusNot(Long partnerId, LocalDate bookingDate, BookingStatus status);
}
