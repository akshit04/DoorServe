package com.doorserve.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PaymentIntentRequest {
    private BigDecimal amount;
    private String currency = "USD";
    private List<CheckoutItem> items;
    private String paymentType; // "direct" or "cart"
    private Long serviceId; // for direct payment
}