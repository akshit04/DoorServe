package com.doorserve.controller;

import com.doorserve.dto.ServiceDetailsDto;
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

    // Services endpoints
    @GetMapping("/api/services")
    public ResponseEntity<List<ServicesCatalog>> getAllServices() {
        return ResponseEntity.ok(servicesCatalogService.findAllServices());
    }

    @GetMapping("/api/services/{id}")
    public ResponseEntity<ServicesCatalog> getServiceById(@PathVariable Long id) {
        return servicesCatalogService.findServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/services/{id}/details")
    public ResponseEntity<ServiceDetailsDto> getServiceDetails(@PathVariable Long id) {
        ServiceDetailsDto serviceDetails = servicesCatalogService.getServiceDetails(id);
        if (serviceDetails == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(serviceDetails);
    }

    @GetMapping("/api/services/category/{category}")
    public ResponseEntity<List<ServicesCatalog>> getServicesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(servicesCatalogService.findServicesByCategory(category));
    }

    // Slug-based endpoints for better URLs
    @GetMapping("/api/services/by-slug/{slug}")
    public ResponseEntity<ServicesCatalog> getServiceBySlug(@PathVariable String slug) {
        return servicesCatalogService.findServiceBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/services/by-slug/{slug}/details")
    public ResponseEntity<ServiceDetailsDto> getServiceDetailsBySlug(@PathVariable String slug) {
        ServiceDetailsDto serviceDetails = servicesCatalogService.getServiceDetailsBySlug(slug);
        if (serviceDetails == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(serviceDetails);
    }

    @GetMapping("/api/services/category-slug/{categorySlug}")
    public ResponseEntity<List<ServicesCatalog>> getServicesByCategorySlug(@PathVariable String categorySlug) {
        System.out.println("🔍 DEBUG: Looking for services with category slug: " + categorySlug);
        List<ServicesCatalog> services = servicesCatalogService.findServicesByCategorySlug(categorySlug);
        System.out.println("🔍 DEBUG: Found " + services.size() + " services");
        return ResponseEntity.ok(services);
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

    @PutMapping("/api/services/{id}")
    public ResponseEntity<ServicesCatalog> updateService(@PathVariable Long id, @RequestBody ServicesCatalog service) {
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

    @GetMapping("/api/services/search")
    public ResponseEntity<List<ServicesCatalog>> searchServices(@RequestParam String term) {
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

    // Debug endpoint to see all categories and their slugs
    @GetMapping("/api/services/debug/categories")
    public ResponseEntity<java.util.Map<String, String>> getDebugCategories() {
        List<ServicesCatalog> allServices = servicesCatalogService.findAllServices();
        java.util.Map<String, String> categoryToSlug = new java.util.HashMap<>();
        
        allServices.stream()
                .map(ServicesCatalog::getCategory)
                .distinct()
                .forEach(category -> {
                    String slug = servicesCatalogService.createSlugPublic(category);
                    categoryToSlug.put(category, slug);
                });
        
        return ResponseEntity.ok(categoryToSlug);
    }
}