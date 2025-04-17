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
    
    /**
     * Find featured services ordered by rating
     * @return List of featured services sorted by rating in descending order
     */
    List<ServicesCatalog> findByFeaturedTrueOrderByRatingDesc();
    
    /**
     * Find services with price less than or equal to the specified amount
     * @param price The maximum price
     * @return List of services with price <= the specified amount
     */
    List<ServicesCatalog> findByPriceLessThanEqual(Double price);
    
    /**
     * Find services with duration less than or equal to the specified minutes
     * @param duration The maximum duration in minutes
     * @return List of services with duration <= the specified minutes
     */
    List<ServicesCatalog> findByDurationLessThanEqual(Integer duration);
}
