package com.doorserve.service;

import com.doorserve.repository.ServicesCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ServicesCatalogRepository servicesCatalogRepository;

    public List<Map<String, Object>> getAllCategories() {
        // Get distinct categories from services
        List<String> categoryNames = servicesCatalogRepository.findDistinctCategories();
        
        return categoryNames.stream()
                .map(this::createCategoryInfo)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getCategoryByName(String categoryName) {
        // Check if category exists
        List<String> categories = servicesCatalogRepository.findDistinctCategories();
        boolean exists = categories.stream()
                .anyMatch(cat -> cat.equalsIgnoreCase(categoryName));
        
        if (!exists) {
            return null;
        }
        
        return createCategoryInfo(categoryName);
    }

    private Map<String, Object> createCategoryInfo(String categoryName) {
        Map<String, Object> category = new HashMap<>();
        category.put("name", categoryName);
        category.put("slug", createSlug(categoryName));
        category.put("description", getCategoryDescription(categoryName));
        category.put("iconUrl", getCategoryIcon(categoryName));
        
        // Count services in this category
        long serviceCount = servicesCatalogRepository.countByCategory(categoryName);
        category.put("serviceCount", serviceCount);
        
        return category;
    }

    /**
     * Convert a string to a URL-friendly slug
     * @param text The text to convert
     * @return URL-friendly slug
     */
    private String createSlug(String text) {
        if (text == null) return "";
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters
                .replaceAll("\\s+", "-") // Replace spaces with hyphens
                .replaceAll("-+", "-") // Replace multiple hyphens with single
                .replaceAll("^-|-$", ""); // Remove leading/trailing hyphens
    }

    private String getCategoryDescription(String categoryName) {
        return switch (categoryName.toLowerCase()) {
            case "cleaning" -> "Professional cleaning services for homes and offices";
            case "plumbing" -> "Expert plumbing repairs and installations";
            case "electrical" -> "Licensed electrical services and repairs";
            case "handyman" -> "General maintenance and repair services";
            case "landscaping" -> "Garden design and outdoor maintenance";
            case "moving" -> "Professional moving and packing services";
            case "pet care" -> "Pet sitting, walking, and grooming services";
            case "security" -> "Home security installation and monitoring";
            default -> "Professional " + categoryName.toLowerCase() + " services";
        };
    }

    private String getCategoryIcon(String categoryName) {
        // Return null for now, can be implemented later with actual icon URLs
        return null;
    }
}