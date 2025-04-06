package com.doorserve.controller;

import com.doorserve.dto.BookingDto;
import com.doorserve.dto.BookingRequest;
import com.doorserve.model.User;
import com.doorserve.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestBody BookingRequest bookingRequest,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(bookingService.createBooking(bookingRequest, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getCurrentUserBookings(
            Authentication authentication,
            @RequestParam(required = false) String status) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(bookingService.getUserBookings(currentUser, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(
            @PathVariable Long id,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(bookingService.getBookingById(id, currentUser));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingDto> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(bookingService.cancelBooking(id, currentUser));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<BookingDto> completeBooking(
            @PathVariable Long id,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(bookingService.completeBooking(id, currentUser));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<BookingDto> rescheduleBooking(
            @PathVariable Long id,
            @RequestBody BookingRequest bookingRequest,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(bookingService.rescheduleBooking(id, bookingRequest, currentUser));
    }
}
