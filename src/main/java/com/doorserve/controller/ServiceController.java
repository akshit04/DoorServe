package com.doorserve.controller;

import com.doorserve.model.ServicesCatalog;
import com.doorserve.service.ServicesCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServiceController {

    private final ServicesCatalogService servicesCatalogService;

    // Services endpoints (frontend expects /api/services/*)
    @GetMapping("/api/services")
    public ResponseEntity<List<ServicesCatalog>> getAllServices() {
        return ResponseEntity.ok(servicesCatalogService.findAllServices());
    }

    // Services-catalog endpoints (frontend also expects /api/services-catalog/*)
    @GetMapping("/api/services-catalog")
    public ResponseEntity<List<ServicesCatalog>> getAllServicesCatalog() {
        return ResponseEntity.ok(servicesCatalogService.findAllServices());
    }

    @GetMapping("/api/services/{id}")
    public ResponseEntity<ServicesCatalog> getServiceById(@PathVariable Long id) {
        return servicesCatalogService.findServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/services-catalog/{id}")
    public ResponseEntity<ServicesCatalog> getServiceCatalogById(@PathVariable Long id) {
        return servicesCatalogService.findServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/services/category/{category}")
    public ResponseEntity<List<ServicesCatalog>> getServicesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(servicesCatalogService.findServicesByCategory(category));
    }

    @GetMapping("/api/services-catalog/category/{category}")
    public ResponseEntity<List<ServicesCatalog>> getServicesCatalogByCategory(@PathVariable String category) {
        return ResponseEntity.ok(servicesCatalogService.findServicesByCategory(category));
    }

    @GetMapping("/api/services/provider/{providerId}")
    public ResponseEntity<List<ServicesCatalog>> getServicesByProviderId(@PathVariable Long providerId) {
        // For now, return all services. This can be implemented later when provider relationship is established
        return ResponseEntity.ok(servicesCatalogService.findAllServices());
    }

    @PostMapping("/api/services")
    public ResponseEntity<ServicesCatalog> createService(@RequestBody ServicesCatalog service) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicesCatalogService.createService(service));
    }

    @PostMapping("/api/services-catalog")
    public ResponseEntity<ServicesCatalog> createServiceCatalog(@RequestBody ServicesCatalog service) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicesCatalogService.createService(service));
    }

    @PutMapping("/api/services/{id}")
    public ResponseEntity<ServicesCatalog> updateService(@PathVariable Long id, @RequestBody ServicesCatalog service) {
        try {
            return ResponseEntity.ok(servicesCatalogService.updateService(id, service));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/api/services-catalog/{id}")
    public ResponseEntity<ServicesCatalog> updateServicesCatalog(@PathVariable Long id, @RequestBody ServicesCatalog service) {
        try {
            return ResponseEntity.ok(servicesCatalogService.updateService(id, service));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        if (servicesCatalogService.deleteService(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/services-catalog/{id}")
    public ResponseEntity<Void> deleteServicesCatalog(@PathVariable Long id) {
        if (servicesCatalogService.deleteService(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/api/services/search")
    public ResponseEntity<List<ServicesCatalog>> searchServices(@RequestParam String term) {
        return ResponseEntity.ok(servicesCatalogService.searchServices(term));
    }

    @GetMapping("/api/services-catalog/search")
    public ResponseEntity<List<ServicesCatalog>> searchServicesCatalog(@RequestParam String term) {
        return ResponseEntity.ok(servicesCatalogService.searchServices(term));
    }

    @GetMapping("/api/services/featured")
    public ResponseEntity<List<ServicesCatalog>> getFeaturedServices(@RequestParam(defaultValue = "5") int limit) {
        List<ServicesCatalog> allServices = servicesCatalogService.findAllServices();
        // Return first 'limit' services as featured for now
        List<ServicesCatalog> featuredServices = allServices.stream()
                .limit(limit)
                .toList();
        return ResponseEntity.ok(featuredServices);
    }
}