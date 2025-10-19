package com.doorserve.config;

import com.doorserve.model.Address;
import com.doorserve.model.Booking;
import com.doorserve.model.BookingStatus;
import com.doorserve.model.PartnerService;
import com.doorserve.model.Review;
import com.doorserve.model.ServicesCatalog;
import com.doorserve.model.User;
import com.doorserve.model.UserType;
import com.doorserve.repository.BookingRepository;
import com.doorserve.repository.PartnerServiceRepository;
import com.doorserve.repository.ReviewRepository;
import com.doorserve.repository.ServicesCatalogRepository;
import com.doorserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServicesCatalogRepository servicesCatalogRepository;
    private final PartnerServiceRepository partnerServiceRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
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

        // Create partner services (linking partners to services)
        createPartnerServices();

        // Create sample bookings and reviews
        createSampleBookingsAndReviews();

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
                        "258 Garden Way", "Sunnyvale", "CA", "94086"));

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
                createService("House Cleaning",
                        "Professional house cleaning service including all rooms, bathrooms, and kitchen", "Cleaning"),
                createService("Deep Cleaning",
                        "Comprehensive deep cleaning service for move-in/move-out or seasonal cleaning", "Cleaning"),
                createService("Office Cleaning",
                        "Commercial office cleaning service for businesses and co-working spaces", "Cleaning"),
                createService("Carpet Cleaning",
                        "Professional carpet and upholstery cleaning using eco-friendly products", "Cleaning"),

                // Plumbing Services
                createService("Leak Repair", "Fix leaky faucets, pipes, and water damage repair", "Plumbing"),
                createService("Drain Cleaning", "Unclog drains, sinks, and sewer line cleaning", "Plumbing"),
                createService("Toilet Installation", "Install new toilets or repair existing toilet issues",
                        "Plumbing"),
                createService("Water Heater Service", "Water heater installation, repair, and maintenance", "Plumbing"),

                // Electrical Services
                createService("Electrical Repair", "Fix electrical outlets, switches, and wiring issues", "Electrical"),
                createService("Light Installation", "Install ceiling fans, light fixtures, and outdoor lighting",
                        "Electrical"),
                createService("Panel Upgrade", "Electrical panel upgrades and circuit breaker installation",
                        "Electrical"),
                createService("Smart Home Setup", "Install smart switches, thermostats, and home automation",
                        "Electrical"),

                // Handyman Services
                createService("Furniture Assembly", "Assemble furniture, shelves, and home organization systems",
                        "Handyman"),
                createService("Wall Mounting", "Mount TVs, artwork, mirrors, and shelving on walls", "Handyman"),
                createService("Door & Window Repair", "Fix doors, windows, locks, and weatherstripping", "Handyman"),
                createService("Drywall Repair", "Patch holes, cracks, and paint touch-ups", "Handyman"),

                // Landscaping Services
                createService("Lawn Mowing", "Regular lawn mowing and grass maintenance service", "Landscaping"),
                createService("Garden Design", "Design and plant new gardens, flower beds, and landscaping",
                        "Landscaping"),
                createService("Tree Trimming", "Prune trees, remove branches, and tree health maintenance",
                        "Landscaping"),
                createService("Sprinkler Installation", "Install and repair irrigation systems and sprinklers",
                        "Landscaping"),

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
                createService("Lock Installation", "Install new locks, deadbolts, and security hardware", "Security"));

        servicesCatalogRepository.saveAll(services);
        log.info("Created {} sample services", services.size());
    }

    private ServicesCatalog createService(String name, String description, String category) {
        ServicesCatalog service = new ServicesCatalog();
        service.setName(name);
        service.setDescription(description);
        service.setCategory(category);

        // Set suggested duration based on service type
        service.setBaseDuration(getSampleDuration(name));

        // Set availability and featured status
        service.setAvailable(true);
        service.setFeatured(Math.random() < 0.3); // 30% chance of being featured

        return service;
    }

    private BigDecimal getSamplePrice(String category) {
        String cat = category.toLowerCase();
        if ("cleaning".equals(cat)) {
            return BigDecimal.valueOf(80 + (Math.random() * 120)); // $80-200
        } else if ("plumbing".equals(cat)) {
            return BigDecimal.valueOf(90 + (Math.random() * 110)); // $90-200
        } else if ("electrical".equals(cat)) {
            return BigDecimal.valueOf(100 + (Math.random() * 150)); // $100-250
        } else if ("handyman".equals(cat)) {
            return BigDecimal.valueOf(60 + (Math.random() * 90)); // $60-150
        } else if ("landscaping".equals(cat)) {
            return BigDecimal.valueOf(70 + (Math.random() * 130)); // $70-200
        } else if ("moving".equals(cat)) {
            return BigDecimal.valueOf(150 + (Math.random() * 250)); // $150-400
        } else if ("pet care".equals(cat)) {
            return BigDecimal.valueOf(25 + (Math.random() * 75)); // $25-100
        } else if ("security".equals(cat)) {
            return BigDecimal.valueOf(120 + (Math.random() * 180)); // $120-300
        } else {
            return BigDecimal.valueOf(75 + (Math.random() * 125)); // $75-200
        }
    }

    private Integer getSampleDuration(String serviceName) {
        String name = serviceName.toLowerCase();
        if ("house cleaning".equals(name) || "deep cleaning".equals(name)) {
            return 180 + (int) (Math.random() * 120); // 3-5 hours
        } else if ("office cleaning".equals(name)) {
            return 120 + (int) (Math.random() * 180); // 2-5 hours
        } else if ("local moving".equals(name)) {
            return 240 + (int) (Math.random() * 240); // 4-8 hours
        } else if ("garden design".equals(name) || "tree trimming".equals(name)) {
            return 120 + (int) (Math.random() * 180); // 2-5 hours
        } else if ("dog walking".equals(name)) {
            return 30 + (int) (Math.random() * 30); // 30-60 minutes
        } else if ("pet grooming".equals(name)) {
            return 60 + (int) (Math.random() * 60); // 1-2 hours
        } else {
            return 60 + (int) (Math.random() * 120); // 1-3 hours
        }
    }

    private void createPartnerServices() {
        List<User> partners = userRepository.findByUserType(UserType.PARTNER);
        List<ServicesCatalog> services = servicesCatalogRepository.findAll();

        if (partners.isEmpty() || services.isEmpty()) {
            log.warn("No partners or services found, skipping partner services creation");
            return;
        }

        List<PartnerService> partnerServices = new java.util.ArrayList<>();

        // Create partner services - each partner offers 2-4 random services
        for (User partner : partners) {
            // Shuffle services and pick 2-4 for each partner
            java.util.Collections.shuffle(services);
            int serviceCount = 2 + (int) (Math.random() * 3); // 2-4 services per partner

            for (int i = 0; i < Math.min(serviceCount, services.size()); i++) {
                ServicesCatalog service = services.get(i);

                PartnerService partnerService = new PartnerService();
                partnerService.setPartner(partner);
                partnerService.setServiceCatalog(service);

                // Set partner-specific pricing based on service category
                BigDecimal basePrice = getSamplePrice(service.getCategory());
                double variation = 0.8 + (Math.random() * 0.4); // 0.8 to 1.2 multiplier
                partnerService.setPrice(basePrice.multiply(BigDecimal.valueOf(variation)));

                // Set partner-specific duration (±30 minutes of base duration)
                int baseDuration = service.getBaseDuration() != null ? service.getBaseDuration() : 60;
                int durationVariation = -30 + (int) (Math.random() * 61); // -30 to +30 minutes
                partnerService.setDuration(Math.max(30, baseDuration + durationVariation));

                // Set partner's custom title for their service offering
                partnerService.setTitle(partner.getFirstName() + "'s " + service.getName());

                // Set partner-specific details
                partnerService.setDescription(getPartnerServiceDescription(partner, service));
                partnerService.setExperienceYears(1 + (int) (Math.random() * 10)); // 1-10 years
                partnerService.setRating(3.5 + (Math.random() * 1.5)); // 3.5-5.0 rating
                partnerService.setTotalJobs((int) (Math.random() * 200)); // 0-200 jobs
                partnerService.setAvailable(Math.random() > 0.1); // 90% available

                partnerServices.add(partnerService);
            }
        }

        partnerServiceRepository.saveAll(partnerServices);
        log.info("Created {} partner services", partnerServices.size());
    }

    private String getPartnerServiceDescription(User partner, ServicesCatalog service) {
        String[] descriptions = {
                "Professional " + service.getName().toLowerCase()
                        + " service with attention to detail and customer satisfaction.",
                "Experienced " + service.getName().toLowerCase() + " specialist with "
                        + (1 + (int) (Math.random() * 10)) + " years in the field.",
                "High-quality " + service.getName().toLowerCase() + " service using premium tools and techniques.",
                "Reliable and efficient " + service.getName().toLowerCase() + " with flexible scheduling options.",
                "Expert " + service.getName().toLowerCase() + " provider committed to exceeding your expectations."
        };
        return descriptions[(int) (Math.random() * descriptions.length)];
    }

    private void createSampleBookingsAndReviews() {
        List<User> customers = userRepository.findByUserType(UserType.CUSTOMER);
        List<User> partners = userRepository.findByUserType(UserType.PARTNER);
        List<ServicesCatalog> services = servicesCatalogRepository.findAll();

        if (customers.isEmpty() || partners.isEmpty() || services.isEmpty()) {
            log.warn("No customers, partners, or services found, skipping bookings and reviews creation");
            return;
        }

        List<Booking> bookings = new java.util.ArrayList<>();
        List<Review> reviews = new java.util.ArrayList<>();

        // Create 15-20 sample bookings (some completed, some in progress)
        for (int i = 0; i < 20; i++) {
            User customer = customers.get((int) (Math.random() * customers.size()));
            User partner = partners.get((int) (Math.random() * partners.size()));
            ServicesCatalog service = services.get((int) (Math.random() * services.size()));

            Booking booking = new Booking();
            booking.setCustomer(customer);
            booking.setPartner(partner);
            // We'll set the partner service later

            // Random booking date in the past 30 days
            java.time.LocalDate bookingDate = java.time.LocalDate.now().minusDays((int) (Math.random() * 30));
            booking.setBookingDate(bookingDate);

            // Random times
            java.time.LocalTime startTime = java.time.LocalTime.of(8 + (int) (Math.random() * 10), 0); // 8 AM to 6 PM
            booking.setStartTime(startTime);
            booking.setEndTime(startTime.plusHours(2)); // 2 hour duration

            // Find a partner service for this booking
            Optional<PartnerService> partnerServiceOpt = partnerServiceRepository
                    .findByPartnerAndServiceCatalog(partner, service);
            if (partnerServiceOpt.isEmpty()) {
                continue; // Skip this booking if no partner service exists
            }
            PartnerService partnerService = partnerServiceOpt.get();

            // Set the partner service for this booking
            booking.setPartnerService(partnerService);

            // Pricing from partner service
            BigDecimal price = partnerService.getPrice();
            booking.setPrice(price);
            booking.setTotalPrice(price);
            booking.setDuration(120); // 2 hours

            // Status - 70% completed, 20% confirmed, 10% pending
            double statusRandom = Math.random();
            if (statusRandom < 0.7) {
                booking.setStatus(BookingStatus.COMPLETED);
            } else if (statusRandom < 0.9) {
                booking.setStatus(BookingStatus.CONFIRMED);
            } else {
                booking.setStatus(BookingStatus.PENDING);
            }

            bookings.add(booking);
        }

        // Save bookings first
        bookings = bookingRepository.saveAll(bookings);
        log.info("Created {} sample bookings", bookings.size());

        // Create reviews for completed bookings (80% of completed bookings get reviews)
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.COMPLETED && Math.random() < 0.8) {
                Review review = new Review();
                review.setBooking(booking);
                review.setCustomer(booking.getCustomer());
                review.setPartner(booking.getPartner());
                review.setPartnerService(booking.getPartnerService());

                // Random rating (weighted towards higher ratings)
                int rating;
                double ratingRandom = Math.random();
                if (ratingRandom < 0.4)
                    rating = 5; // 40% - 5 stars
                else if (ratingRandom < 0.7)
                    rating = 4; // 30% - 4 stars
                else if (ratingRandom < 0.85)
                    rating = 3; // 15% - 3 stars
                else if (ratingRandom < 0.95)
                    rating = 2; // 10% - 2 stars
                else
                    rating = 1; // 5% - 1 star

                review.setRating(rating);
                review.setComment(generateReviewComment(rating, booking.getPartnerService().getTitle()));

                reviews.add(review);
            }
        }

        reviewRepository.saveAll(reviews);
        log.info("Created {} sample reviews", reviews.size());
    }

    private String generateReviewComment(int rating, String serviceName) {
        String[][] comments = {
                // 1 star
                {
                        "Very disappointed with the " + serviceName.toLowerCase()
                                + ". Poor quality and unprofessional service.",
                        "Would not recommend. The service was below expectations and the provider was late.",
                        "Terrible experience. Had to redo the work myself. Complete waste of money."
                },
                // 2 stars
                {
                        "The " + serviceName.toLowerCase() + " was okay but had several issues. Could be better.",
                        "Service was completed but not to the standard I expected. Some problems with quality.",
                        "Average service with room for improvement. Provider was friendly but work quality was lacking."
                },
                // 3 stars
                {
                        "Decent " + serviceName.toLowerCase() + " service. Got the job done but nothing exceptional.",
                        "Good service overall. A few minor issues but generally satisfied with the results.",
                        "Fair quality work. The provider was professional and completed the job on time."
                },
                // 4 stars
                {
                        "Great " + serviceName.toLowerCase() + " service! Very professional and high quality work.",
                        "Excellent service with attention to detail. Would definitely use again.",
                        "Very satisfied with the quality and professionalism. Highly recommend this provider."
                },
                // 5 stars
                {
                        "Outstanding " + serviceName.toLowerCase() + " service! Exceeded all expectations. Perfect!",
                        "Absolutely fantastic work! Professional, punctual, and exceptional quality. 10/10!",
                        "Best " + serviceName.toLowerCase() + " service I've ever received. Will definitely book again!"
                }
        };

        String[] ratingComments = comments[rating - 1];
        return ratingComments[(int) (Math.random() * ratingComments.length)];
    }
}