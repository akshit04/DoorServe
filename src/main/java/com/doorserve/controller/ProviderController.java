package com.doorserve.controller;

import com.doorserve.dto.ProviderServiceDetailsDto;
import com.doorserve.dto.OtherServiceDto;
import com.doorserve.model.User;
import com.doorserve.service.ProviderService;
import com.doorserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;
    private final UserRepository userRepository;

    @GetMapping("/{partnerId}")
    public ResponseEntity<User> getProviderProfile(@PathVariable Long partnerId) {
        return userRepository.findById(partnerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{partnerId}/services/{serviceId}")
    public ResponseEntity<ProviderServiceDetailsDto> getProviderServiceDetails(
            @PathVariable Long partnerId, 
            @PathVariable Long serviceId) {
        
        ProviderServiceDetailsDto details = providerService.getProviderServiceDetails(partnerId, serviceId);
        if (details == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }

    @GetMapping("/{partnerId}/services")
    public ResponseEntity<List<OtherServiceDto>> getProviderServices(@PathVariable Long partnerId) {
        List<OtherServiceDto> services = providerService.getProviderServices(partnerId);
        return ResponseEntity.ok(services);
    }
}