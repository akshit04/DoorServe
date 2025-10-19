package com.doorserve.service;

import com.doorserve.dto.BookingDto;
import com.doorserve.dto.BookingRequest;
import com.doorserve.exception.ResourceNotFoundException;
import com.doorserve.exception.UnauthorizedException;
import com.doorserve.model.Booking;
import com.doorserve.model.BookingStatus;
import com.doorserve.model.ServicesCatalog;
import com.doorserve.model.User;
import com.doorserve.model.UserType;
import com.doorserve.repository.BookingRepository;
import com.doorserve.repository.ServicesCatalogRepository;
import com.doorserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServicesCatalogRepository servicesCatalogRepository;

    public BookingDto createBooking(BookingRequest request, User currentUser) {
        if (currentUser.getUserType() != UserType.CUSTOMER) {
            throw new UnauthorizedException("Only customers can create bookings");
        }

        User partner = userRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found"));

        if (partner.getUserType() != UserType.PARTNER) {
            throw new ResourceNotFoundException("Selected user is not a service partner");
        }

        ServicesCatalog serviceCatalog = servicesCatalogRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceCatalog not found"));

        // Calculate end time based on service duration
        LocalTime endTime = request.getStartTime().plusMinutes(request.getDuration());

        // Check if the partner is available at the requested time
        // This would involve checking for overlap with existing bookings
        checkPartnerAvailability(partner.getId(), request.getBookingDate(), request.getStartTime(), endTime);

        Booking booking = new Booking();
        booking.setCustomer(currentUser);
        booking.setPartner(partner);
        // We need to find the partner service for this booking
        // For now, we'll need to update the booking creation logic
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(endTime);
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(request.getPrice());

        Booking savedBooking = bookingRepository.save(booking);
        
        return mapToDto(savedBooking);
    }

    public List<BookingDto> getUserBookings(User currentUser, String status) {
        List<Booking> bookings;
        
        if (currentUser.getUserType() == UserType.CUSTOMER) {
            bookings = status != null 
                ? bookingRepository.findByCustomerAndStatus(currentUser, BookingStatus.valueOf(status.toUpperCase()))
                : bookingRepository.findByCustomer(currentUser);
        } else if (currentUser.getUserType() == UserType.PARTNER) {
            bookings = status != null 
                ? bookingRepository.findByPartnerAndStatus(currentUser, BookingStatus.valueOf(status.toUpperCase()))
                : bookingRepository.findByPartner(currentUser);
        } else {
            bookings = status != null 
                ? bookingRepository.findByStatus(BookingStatus.valueOf(status.toUpperCase()))
                : bookingRepository.findAll();
        }
        
        return bookings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BookingDto getBookingById(Long id, User currentUser) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        // Verify user has access to this booking
        if (currentUser.getUserType() != UserType.ADMIN && 
            !booking.getCustomer().getId().equals(currentUser.getId()) && 
            !booking.getPartner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have access to this booking");
        }
        
        return mapToDto(booking);
    }

    public BookingDto cancelBooking(Long id, User currentUser) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        // Verify user has access to cancel this booking
        if (currentUser.getUserType() != UserType.ADMIN && 
            !booking.getCustomer().getId().equals(currentUser.getId()) && 
            !booking.getPartner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to cancel this booking");
        }
        
        // Check if booking can be cancelled
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed booking");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        
        return mapToDto(updatedBooking);
    }

    public BookingDto completeBooking(Long id, User currentUser) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        // Only partners or admins can mark bookings as complete
        if (currentUser.getUserType() != UserType.ADMIN && 
            !booking.getPartner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the service provider or admin can mark a booking as complete");
        }
        
        // Check if booking can be completed
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be marked as complete");
        }
        
        booking.setStatus(BookingStatus.COMPLETED);
        Booking updatedBooking = bookingRepository.save(booking);
        
        return mapToDto(updatedBooking);
    }

    public BookingDto rescheduleBooking(Long id, BookingRequest request, User currentUser) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        // Verify user has access to reschedule this booking
        if (currentUser.getUserType() != UserType.ADMIN && 
            !booking.getCustomer().getId().equals(currentUser.getId()) && 
            !booking.getPartner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to reschedule this booking");
        }
        
        // Check if booking can be rescheduled
        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot reschedule a completed or cancelled booking");
        }
        
        // Calculate end time based on service duration
        LocalTime endTime = request.getStartTime().plusMinutes(request.getDuration());
        
        // Check partner availability
        checkPartnerAvailability(booking.getPartner().getId(), request.getBookingDate(), 
                                request.getStartTime(), endTime);
        
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(endTime);
        
        Booking updatedBooking = bookingRepository.save(booking);
        
        return mapToDto(updatedBooking);
    }

    private void checkPartnerAvailability(Long partnerId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Booking> existingBookings = bookingRepository.findByPartnerIdAndBookingDateAndStatusNot(
                partnerId, date, BookingStatus.CANCELLED);
        
        boolean isOverlapping = existingBookings.stream().anyMatch(booking -> 
                (startTime.isBefore(booking.getEndTime()) && endTime.isAfter(booking.getStartTime())));
        
        if (isOverlapping) {
            throw new IllegalStateException("The partner is not available at the requested time");
        }
    }

    private BookingDto mapToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer().getId())
                .customerName(booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName())
                .partnerId(booking.getPartner().getId())
                .partnerName(booking.getPartner().getFirstName() + " " + booking.getPartner().getLastName())
                .serviceId(booking.getPartnerService().getServiceCatalog().getId())
                .serviceName(booking.getPartnerService().getTitle())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .build();
    }
}
