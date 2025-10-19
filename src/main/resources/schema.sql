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

-- CREATE TABLE IF NOT EXISTS partner_services (
--     id SERIAL PRIMARY KEY,
--     partner_id BIGINT NOT NULL REFERENCES users(id),
--     service_id BIGINT NOT NULL REFERENCES services(id),
--     price DECIMAL(10, 2),
--     duration INTEGER,
--     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
--     updated_at TIMESTAMP,
--     UNIQUE(partner_id, service_id)
-- );

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

-- CREATE TABLE IF NOT EXISTS reviews (
--     id SERIAL PRIMARY KEY,
--     booking_id BIGINT NOT NULL REFERENCES bookings(id),
--     customer_id BIGINT NOT NULL REFERENCES users(id),
--     partner_id BIGINT NOT NULL REFERENCES users(id),
--     rating INTEGER NOT NULL,
--     comment TEXT,
--     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
--     updated_at TIMESTAMP
-- );
