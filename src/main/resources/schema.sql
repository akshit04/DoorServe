CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    user_type VARCHAR(20) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    street VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS services_catalog (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2),
    duration INTEGER,
    rating DECIMAL(3, 2),
    image_url VARCHAR(255),
    available BOOLEAN DEFAULT true,
    featured BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS partner_services (
    id SERIAL PRIMARY KEY,
    partner_id BIGINT NOT NULL REFERENCES users(id),
    service_catalog_id BIGINT NOT NULL REFERENCES services_catalog(id),
    price DECIMAL(10, 2) NOT NULL,
    duration INTEGER NOT NULL,
    description TEXT,
    available BOOLEAN DEFAULT true,
    experience_years INTEGER DEFAULT 0,
    rating DECIMAL(3, 2) DEFAULT 0.0,
    total_jobs INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    UNIQUE(partner_id, service_catalog_id)
);

CREATE TABLE IF NOT EXISTS cart (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    service_id BIGINT NOT NULL REFERENCES services_catalog(id),
    quantity INTEGER NOT NULL DEFAULT 1,
    price DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    UNIQUE(user_id, service_id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id SERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES users(id),
    partner_id BIGINT NOT NULL REFERENCES users(id),
    service_catalog_id BIGINT NOT NULL REFERENCES services_catalog(id),
    booking_date DATE NOT NULL,
    price DECIMAL(10, 2),
    duration INTEGER,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_user_type ON users(user_type);
CREATE INDEX IF NOT EXISTS idx_services_catalog_category ON services_catalog(category);
CREATE INDEX IF NOT EXISTS idx_services_catalog_available ON services_catalog(available);
CREATE INDEX IF NOT EXISTS idx_services_catalog_featured ON services_catalog(featured);
CREATE INDEX IF NOT EXISTS idx_partner_services_partner_id ON partner_services(partner_id);
CREATE INDEX IF NOT EXISTS idx_partner_services_service_catalog_id ON partner_services(service_catalog_id);
CREATE INDEX IF NOT EXISTS idx_partner_services_available ON partner_services(available);
CREATE INDEX IF NOT EXISTS idx_cart_user_id ON cart(user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_customer_id ON bookings(customer_id);
CREATE INDEX IF NOT EXISTS idx_bookings_partner_id ON bookings(partner_id);
CREATE INDEX IF NOT EXISTS idx_bookings_status ON bookings(status);
CREATE INDEX IF NOT EXISTS idx_bookings_booking_date ON bookings(booking_date);

CREATE TABLE IF NOT EXISTS reviews (
    id SERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id),
    customer_id BIGINT NOT NULL REFERENCES users(id),
    partner_id BIGINT NOT NULL REFERENCES users(id),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    UNIQUE(booking_id, customer_id)
);

-- Additional indexes for reviews
CREATE INDEX IF NOT EXISTS idx_reviews_booking_id ON reviews(booking_id);
CREATE INDEX IF NOT EXISTS idx_reviews_customer_id ON reviews(customer_id);
CREATE INDEX IF NOT EXISTS idx_reviews_partner_id ON reviews(partner_id);
CREATE INDEX IF NOT EXISTS idx_reviews_rating ON reviews(rating);
