package com.doorserve.service;

import com.doorserve.dto.ProviderServiceDetailsDto;
import com.doorserve.dto.OtherServiceDto;
import com.doorserve.model.User;
import com.doorserve.model.PartnerService;
import com.doorserve.repository.UserRepository;
import com.doorserve.repository.PartnerServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final UserRepository userRepository;
    private final PartnerServiceRepository partnerServiceRepository;

    public ProviderServiceDetailsDto getProviderServiceDetails(Long partnerId, Long serviceId) {
        // Get provider details
        Optional<User> providerOpt = userRepository.findById(partnerId);
        if (providerOpt.isEmpty()) {
            return null;
        }

        // Get partner service details
        Optional<PartnerService> partnerServiceOpt = partnerServiceRepository
                .findByPartnerIdAndServiceCatalogId(partnerId, serviceId);
        if (partnerServiceOpt.isEmpty()) {
            return null;
        }

        User provider = providerOpt.get();
        PartnerService partnerService = partnerServiceOpt.get();

        // Get other services by this provider (excluding current service)
        List<OtherServiceDto> otherServices = partnerServiceRepository
                .findByPartnerId(partnerId)
                .stream()
                .filter(ps -> !ps.getServiceCatalogId().equals(serviceId))
                .map(ps -> new OtherServiceDto(
                        ps.getServiceCatalogId(),
                        ps.getServicesCatalog().getName(),
                        ps.getServicesCatalog().getCategory(),
                        ps.getPrice(),
                        ps.getDuration()
                ))
                .collect(Collectors.toList());

        return new ProviderServiceDetailsDto(provider, partnerService, otherServices);
    }

    public List<OtherServiceDto> getProviderServices(Long partnerId) {
        return partnerServiceRepository.findByPartnerId(partnerId)
                .stream()
                .map(ps -> new OtherServiceDto(
                        ps.getServiceCatalogId(),
                        ps.getServicesCatalog().getName(),
                        ps.getServicesCatalog().getCategory(),
                        ps.getPrice(),
                        ps.getDuration()
                ))
                .collect(Collectors.toList());
    }
}