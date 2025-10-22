package com.doorserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private Long partnerServiceId;
    private ServiceDTO service;
    private ProviderDTO provider;
    private String title;
    private Integer quantity;
    private BigDecimal price;
    private Integer duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceDTO {
        private Long id;
        private String name;
        private String description;
        private String category;
        private String imageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderDTO {
        private Long id;
        private String name;
        private Double rating;
        private Integer experienceYears;
    }
}