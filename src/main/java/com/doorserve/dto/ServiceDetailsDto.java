package com.doorserve.dto;

import com.doorserve.model.ServicesCatalog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDetailsDto {
    private ServicesCatalog service;
    private List<PartnerServiceDto> availablePartners;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer totalPartners;
    private Double averageRating;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartnerServiceDto {
        private Long id;
        private Long partnerId;
        private String partnerName;
        private String partnerEmail;
        private BigDecimal price;
        private Integer duration;
        private String description;
        private Integer experienceYears;
        private Double rating;
        private Integer totalJobs;
        private Boolean available;
    }
}