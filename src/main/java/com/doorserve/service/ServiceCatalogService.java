package com.doorserve.service;

import com.doorserve.model.Service;
import com.doorserve.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceCatalogService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /**
     * Find all available services
     * @return List of all services
     */
    public List<Service> findAllServices() {
        return serviceRepository.findAll();
    }

    /**
     * Find services by category
     * @param category The service category
     * @return List of services in the given category
     */
    public List<Service> findServicesByCategory(String category) {
        return serviceRepository.findByCategory(category);
    }

    /**
     * Find service by ID
     * @param id The service ID
     * @return The service if found
     */
    public Optional<Service> findServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    /**
     * Find services by provider ID
     * @param providerId The service provider ID
     * @return List of services offered by the provider
     */
    public List<Service> findServicesByProviderId(Long providerId) {
        return serviceRepository.findByProviderId(providerId);
    }

    /**
     * Create a new service
     * @param service The service to create
     * @return The created service
     */
    @Transactional
    public Service createService(Service service) {
        return serviceRepository.save(service);
    }

    /**
     * Update an existing service
     * @param id The service ID
     * @param serviceDetails The updated service details
     * @return The updated service
     * @throws RuntimeException if service not found
     */
    @Transactional
    public Service updateService(Long id, Service serviceDetails) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
        
        service.setName(serviceDetails.getName());
        service.setDescription(serviceDetails.getDescription());
        service.setPrice(serviceDetails.getPrice());
        service.setDuration(serviceDetails.getDuration());
        service.setCategory(serviceDetails.getCategory());
        service.setProviderId(serviceDetails.getProviderId());
        service.setAvailable(serviceDetails.isAvailable());
        
        return serviceRepository.save(service);
    }

    /**
     * Delete a service
     * @param id The service ID
     * @return true if deleted, false otherwise
     */
    @Transactional
    public boolean deleteService(Long id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Search services by name or description containing the search term
     * @param searchTerm The search term
     * @return List of matching services
     */
    public List<Service> searchServices(String searchTerm) {
        return serviceRepository.findByNameContainingOrDescriptionContaining(
                searchTerm, searchTerm);
    }

    /**
     * Find featured or recommended services
     * @param limit Maximum number of services to return
     * @return List of featured services
     */
    public List<Service> findFeaturedServices(int limit) {
        return serviceRepository.findByFeaturedTrueOrderByRatingDesc()
                .stream()
                .limit(limit)
                .toList();
    }
}
