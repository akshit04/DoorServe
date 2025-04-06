package com.doorserve.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingRequest {
    private Long partnerId;
    private Long serviceId;
    private LocalDate bookingDate;
    private LocalTime startTime;
}
