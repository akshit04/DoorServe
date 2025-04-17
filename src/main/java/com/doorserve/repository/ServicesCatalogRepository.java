package com.doorserve.repository;

import com.doorserve.model.ServicesCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicesCatalogRepository extends JpaRepository<ServicesCatalog, Long> {
    /**
     * Find services by category
     * @param category The service category
     * @return List of services in the given category
     */
    List<ServicesCatalog> findByCategory(String category);
    
    /**
     * Search services by name or description containing the search term
     * @param name The name search term
     * @param description The description search term
     * @return List of matching services
     */
    List<ServicesCatalog> findByNameContainingOrDescriptionContaining(String name, String description);
}
