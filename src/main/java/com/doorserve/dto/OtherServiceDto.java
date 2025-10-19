package com.doorserve.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtherServiceDto {
    private Long serviceId;
    private String serviceName;
    private String category;
    private BigDecimal price;
    private Integer duration;
}