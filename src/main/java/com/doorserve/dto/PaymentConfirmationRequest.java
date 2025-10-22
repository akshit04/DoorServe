package com.doorserve.dto;

import lombok.Data;

@Data
public class PaymentConfirmationRequest {
    private String paymentIntentId;
    private Long orderId;
}