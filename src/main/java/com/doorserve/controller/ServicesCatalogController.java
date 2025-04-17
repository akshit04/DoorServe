package com.doorserve.controller;

import com.doorserve.model.ServicesCatalog;
import com.doorserve.service.ServicesCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services-catalog")
public class ServicesCatalogController {

    private final ServicesCatalogService servicesCatalogService;

    @Autowired
    public ServicesCatalogController(ServicesCatalogService servicesCatalogService) {
        this.servicesCatalogService = servicesCatalogService;
    }

    @GetMapping
    public ResponseEntity<List<ServicesCatalog>> getAllServicesCatalog() {
        return ResponseEntity.ok(servicesCatalogService.findAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicesCatalog> getServiceCatalogById(@PathVariable Long id) {
        return servicesCatalogService.findServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ServicesCatalog>> getServicesCatalogByCategory(@PathVariable String category) {
        return ResponseEntity.ok(servicesCatalogService.findServicesByCategory(category));
    }

    @PostMapping
    public ResponseEntity<ServicesCatalog> createService(@RequestBody ServicesCatalog service) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicesCatalogService.createService(service));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicesCatalog> updateServicesCatalog(@PathVariable Long id, @RequestBody ServicesCatalog service) {
        try {
            return ResponseEntity.ok(servicesCatalogService.updateService(id, service));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServicesCatalog(@PathVariable Long id) {
        if (servicesCatalogService.deleteService(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServicesCatalog>> searchServicesCatalog(@RequestParam String term) {
        return ResponseEntity.ok(servicesCatalogService.searchServices(term));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ServicesCatalog>> getFeaturedServicesCatalog(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(servicesCatalogService.findFeaturedServices(limit));
    }
}
