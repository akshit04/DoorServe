package com.doorserve.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.doorserve.model.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long partnerId;
    private String partnerName;
    private Long serviceId;
    private String serviceName;
    private BigDecimal price;
    private Integer duration;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BookingStatus status;
    private BigDecimal totalPrice;
}
