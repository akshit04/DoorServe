package com.doorserve.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CheckoutItem {
    private Long partnerServiceId;
    private Integer quantity = 1;
    private BigDecimal price;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
}