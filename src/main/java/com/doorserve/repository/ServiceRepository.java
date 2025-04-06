package com.doorserve.repository;

import com.doorserve.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    /**
     * Find services by category
     * @param category The service category
     * @return List of services in the given category
     */
    List<Service> findByCategory(String category);
    
    /**
     * Find services by provider ID
     * @param providerId The service provider ID
     * @return List of services offered by the provider
     */
    List<Service> findByProviderId(Long providerId);
    
    /**
     * Search services by name or description containing the search term
     * @param name The name search term
     * @param description The description search term
     * @return List of matching services
     */
    List<Service> findByNameContainingOrDescriptionContaining(String name, String description);
    
    /**
     * Find featured services ordered by rating
     * @return List of featured services sorted by rating in descending order
     */
    List<Service> findByFeaturedTrueOrderByRatingDesc();
    
    /**
     * Find services by availability status
     * @param available The availability status
     * @return List of services with the specified availability
     */
    List<Service> findByAvailable(boolean available);
    
    /**
     * Find services by category and availability
     * @param category The service category
     * @param available The availability status
     * @return List of services in the given category with the specified availability
     */
    List<Service> findByCategoryAndAvailable(String category, boolean available);
    
    /**
     * Find services by provider ID and availability
     * @param providerId The service provider ID
     * @param available The availability status
     * @return List of services offered by the provider with the specified availability
     */
    List<Service> findByProviderIdAndAvailable(Long providerId, boolean available);
    
    /**
     * Find services with price less than or equal to the specified amount
     * @param price The maximum price
     * @return List of services with price <= the specified amount
     */
    List<Service> findByPriceLessThanEqual(Double price);
    
    /**
     * Find services with duration less than or equal to the specified minutes
     * @param duration The maximum duration in minutes
     * @return List of services with duration <= the specified minutes
     */
    List<Service> findByDurationLessThanEqual(Integer duration);
}
