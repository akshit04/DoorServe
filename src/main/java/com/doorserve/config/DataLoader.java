package com.doorserve.config;

import com.doorserve.model.Address;
import com.doorserve.model.ServicesCatalog;
import com.doorserve.model.User;
import com.doorserve.model.UserType;
import com.doorserve.repository.ServicesCatalogRepository;
import com.doorserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServicesCatalogRepository servicesCatalogRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadSampleData();
    }

    private void loadSampleData() {
        log.info("DataLoader started - checking existing data...");
        
        long userCount = userRepository.count();
        long serviceCount = servicesCatalogRepository.count();
        
        log.info("Found {} users and {} services in database", userCount, serviceCount);
        
        // Check if data already exists
        if (userCount > 0 || serviceCount > 0) {
            log.info("Sample data already exists, skipping data loading");
            return;
        }

        log.info("Database is empty, loading sample data...");

        // Create sample users
        createSampleUsers();
        
        // Create sample services
        createSampleServices();

        log.info("Sample data loaded successfully!");
    }

    private void createSampleUsers() {
        List<User> users = Arrays.asList(
            // Customers
            createUser("john.doe@example.com", "John", "Doe", UserType.CUSTOMER, "555-0101", 
                      "123 Main St", "San Francisco", "CA", "94102"),
            createUser("jane.smith@example.com", "Jane", "Smith", UserType.CUSTOMER, "555-0102", 
                      "456 Oak Ave", "San Francisco", "CA", "94103"),
            createUser("mike.johnson@example.com", "Mike", "Johnson", UserType.CUSTOMER, "555-0103", 
                      "789 Pine St", "Oakland", "CA", "94601"),

            // Partners
            createUser("alice.cleaner@example.com", "Alice", "Wilson", UserType.PARTNER, "555-0201", 
                      "321 Service Rd", "San Francisco", "CA", "94104"),
            createUser("bob.plumber@example.com", "Bob", "Martinez", UserType.PARTNER, "555-0202", 
                      "654 Fix St", "San Jose", "CA", "95101"),
            createUser("carol.electrician@example.com", "Carol", "Davis", UserType.PARTNER, "555-0203", 
                      "987 Electric Ave", "Palo Alto", "CA", "94301"),
            createUser("david.handyman@example.com", "David", "Brown", UserType.PARTNER, "555-0204", 
                      "147 Repair Blvd", "Mountain View", "CA", "94041"),
            createUser("emma.gardener@example.com", "Emma", "Green", UserType.PARTNER, "555-0205", 
                      "258 Garden Way", "Sunnyvale", "CA", "94086")
        );

        userRepository.saveAll(users);
        log.info("Created {} sample users", users.size());
    }

    private User createUser(String email, String firstName, String lastName, UserType userType, 
                           String phone, String street, String city, String state, String zipCode) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123")); // Default password for all sample users
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserType(userType);
        user.setPhone(phone);
        
        Address address = new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setState(state);
        address.setZipCode(zipCode);
        user.setAddress(address);
        
        return user;
    }

    private void createSampleServices() {
        List<ServicesCatalog> services = Arrays.asList(
            // Cleaning Services
            createService("House Cleaning", "Professional house cleaning service including all rooms, bathrooms, and kitchen", "Cleaning"),
            createService("Deep Cleaning", "Comprehensive deep cleaning service for move-in/move-out or seasonal cleaning", "Cleaning"),
            createService("Office Cleaning", "Commercial office cleaning service for businesses and co-working spaces", "Cleaning"),
            createService("Carpet Cleaning", "Professional carpet and upholstery cleaning using eco-friendly products", "Cleaning"),

            // Plumbing Services
            createService("Leak Repair", "Fix leaky faucets, pipes, and water damage repair", "Plumbing"),
            createService("Drain Cleaning", "Unclog drains, sinks, and sewer line cleaning", "Plumbing"),
            createService("Toilet Installation", "Install new toilets or repair existing toilet issues", "Plumbing"),
            createService("Water Heater Service", "Water heater installation, repair, and maintenance", "Plumbing"),

            // Electrical Services
            createService("Electrical Repair", "Fix electrical outlets, switches, and wiring issues", "Electrical"),
            createService("Light Installation", "Install ceiling fans, light fixtures, and outdoor lighting", "Electrical"),
            createService("Panel Upgrade", "Electrical panel upgrades and circuit breaker installation", "Electrical"),
            createService("Smart Home Setup", "Install smart switches, thermostats, and home automation", "Electrical"),

            // Handyman Services
            createService("Furniture Assembly", "Assemble furniture, shelves, and home organization systems", "Handyman"),
            createService("Wall Mounting", "Mount TVs, artwork, mirrors, and shelving on walls", "Handyman"),
            createService("Door & Window Repair", "Fix doors, windows, locks, and weatherstripping", "Handyman"),
            createService("Drywall Repair", "Patch holes, cracks, and paint touch-ups", "Handyman"),

            // Landscaping Services
            createService("Lawn Mowing", "Regular lawn mowing and grass maintenance service", "Landscaping"),
            createService("Garden Design", "Design and plant new gardens, flower beds, and landscaping", "Landscaping"),
            createService("Tree Trimming", "Prune trees, remove branches, and tree health maintenance", "Landscaping"),
            createService("Sprinkler Installation", "Install and repair irrigation systems and sprinklers", "Landscaping"),

            // Moving Services
            createService("Local Moving", "Local residential and commercial moving services", "Moving"),
            createService("Packing Service", "Professional packing and unpacking services", "Moving"),
            createService("Storage Solutions", "Temporary storage and warehouse services", "Moving"),

            // Pet Services
            createService("Dog Walking", "Daily dog walking and pet exercise services", "Pet Care"),
            createService("Pet Sitting", "In-home pet sitting and overnight pet care", "Pet Care"),
            createService("Pet Grooming", "Professional pet grooming and bathing services", "Pet Care"),

            // Home Security
            createService("Security System Install", "Install home security cameras and alarm systems", "Security"),
            createService("Lock Installation", "Install new locks, deadbolts, and security hardware", "Security")
        );

        servicesCatalogRepository.saveAll(services);
        log.info("Created {} sample services", services.size());
    }

    private ServicesCatalog createService(String name, String description, String category) {
        ServicesCatalog service = new ServicesCatalog();
        service.setName(name);
        service.setDescription(description);
        service.setCategory(category);
        return service;
    }
}