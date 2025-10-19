package com.doorserve.dto;

import com.doorserve.model.User;
import com.doorserve.model.PartnerService;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderServiceDetailsDto {
    private User provider;
    private PartnerService partnerService;
    private List<OtherServiceDto> otherServices;
}