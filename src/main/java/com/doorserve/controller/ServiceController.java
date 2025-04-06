package com.doorserve.controller;

import com.doorserve.model.Service;
import com.doorserve.service.ServiceCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;

    @Autowired
    public ServiceController(ServiceCatalogService serviceCatalogService) {
        this.serviceCatalogService = serviceCatalogService;
    }

    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        return ResponseEntity.ok(serviceCatalogService.findAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        return serviceCatalogService.findServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Service>> getServicesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(serviceCatalogService.findServicesByCategory(category));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Service>> getServicesByProviderId(@PathVariable Long providerId) {
        return ResponseEntity.ok(serviceCatalogService.findServicesByProviderId(providerId));
    }

    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceCatalogService.createService(service));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody Service service) {
        try {
            return ResponseEntity.ok(serviceCatalogService.updateService(id, service));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        if (serviceCatalogService.deleteService(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Service>> searchServices(@RequestParam String term) {
        return ResponseEntity.ok(serviceCatalogService.searchServices(term));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Service>> getFeaturedServices(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(serviceCatalogService.findFeaturedServices(limit));
    }
}
