package com.doorserve.service;

import com.doorserve.model.ServicesCatalog;
import com.doorserve.repository.ServicesCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServicesCatalogService {

    private final ServicesCatalogRepository servicesCatalogRepository;

    @Autowired
    public ServicesCatalogService(ServicesCatalogRepository servicesCatalogRepository) {
        this.servicesCatalogRepository = servicesCatalogRepository;
    }

    /**
     * Find all available services
     * @return List of all services
     */
    public List<ServicesCatalog> findAllServices() {
        return servicesCatalogRepository.findAll();
    }

    /**
     * Find services by category
     * @param category The service category
     * @return List of services in the given category
     */
    public List<ServicesCatalog> findServicesByCategory(String category) {
        return servicesCatalogRepository.findByCategory(category);
    }

    /**
     * Find service by ID
     * @param id The service ID
     * @return The service if found
     */
    public Optional<ServicesCatalog> findServiceById(Long id) {
        return servicesCatalogRepository.findById(id);
    }

    /**
     * Create a new service
     * @param service The service to create
     * @return The created service
     */
    @Transactional
    public ServicesCatalog createService(ServicesCatalog service) {
        return servicesCatalogRepository.save(service);
    }

    /**
     * Update an existing service
     * @param id The service ID
     * @param serviceDetails The updated service details
     * @return The updated service
     * @throws RuntimeException if service not found
     */
    @Transactional
    public ServicesCatalog updateService(Long id, ServicesCatalog serviceDetails) {
        ServicesCatalog service = servicesCatalogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
        
        service.setName(serviceDetails.getName());
        service.setDescription(serviceDetails.getDescription());
        service.setCategory(serviceDetails.getCategory());
        
        return servicesCatalogRepository.save(service);
    }

    /**
     * Delete a service
     * @param id The service ID
     * @return true if deleted, false otherwise
     */
    @Transactional
    public boolean deleteService(Long id) {
        if (servicesCatalogRepository.existsById(id)) {
            servicesCatalogRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Search services by name or description containing the search term
     * @param searchTerm The search term
     * @return List of matching services
     */
    public List<ServicesCatalog> searchServices(String searchTerm) {
        return servicesCatalogRepository.findByNameContainingOrDescriptionContaining(
                searchTerm, searchTerm);
    }
}
