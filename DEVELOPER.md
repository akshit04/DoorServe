# DoorServe Developer Guide

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 12+

### Application Info
- **App URL**: http://localhost:8080/oauth2/authorization/google
- **Database**: doorserve
- **DB User**: akshit_kharbanda

## 🛠️ Setup & Build

### 1. Environment Setup
Ensure your `.env` file exists in the project root with required variables:
```bash
# Check if .env file exists and contains JWT_SECRET
cat .env | grep JWT_SECRET
```

### 2. Build Commands
```bash
# Clean and build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### 3. Troubleshooting Build Issues
If you encounter `JWT_SECRET` placeholder errors:
```bash
# Verify .env file is properly formatted
cat .env

# Ensure no extra spaces around the = sign
# Correct format: JWT_SECRET=your_secret_here
```

## 🗄️ PostgreSQL Database

### Server Management
```bash
# Start PostgreSQL server
brew services start postgresql

# Stop PostgreSQL server  
brew services stop postgresql

# Restart PostgreSQL server
brew services restart postgresql

# Check server status
brew services list | grep postgresql
```

### Database Connection
```bash
# Login as root user
psql postgres

# Login as specific user
psql -U door-serve-test-user -h localhost -d doorserve
# Password: thats-what-she-said

# Quick connection test
psql postgres -c "\l" | grep doorserve
```

### Essential PostgreSQL Commands

#### Navigation & Information
```sql
-- Exit psql
\q

-- Show all users
\du

-- List all databases
\l

-- Connect to specific database
\c doorserve

-- Show tables in current database
\dt

-- Check table schema
\d+ tablename
```

#### Database Setup
```sql
-- Create the doorserve database
CREATE DATABASE doorserve;

-- Grant user permissions (PostgreSQL syntax)
GRANT ALL PRIVILEGES ON DATABASE doorserve TO akshit_kharbanda;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO akshit_kharbanda;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO akshit_kharbanda;
```

#### Table Management
```sql
-- Example table creation (legacy reference)
CREATE TABLE user (
  document_id VARCHAR(36) UNIQUE PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  first_name VARCHAR(20) NOT NULL,
  last_name VARCHAR(20) NOT NULL,
  password VARCHAR(25) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Delete tables
DROP TABLE IF EXISTS table_1, table_2;
```

## 🔧 Common Issues & Solutions

### Issue: JWT_SECRET placeholder error
**Error**: `Could not resolve placeholder 'JWT_SECRET' in value "${JWT_SECRET}"`

**Solution**: 
The project includes a `DotenvConfig` class that automatically loads `.env` variables. If you still encounter issues:

1. Verify `.env` file exists in project root
2. Check `.env` file format (no spaces around `=`)
3. Ensure the file contains all required variables:
   ```bash
   GOOGLE_CLIENT_ID=your_google_client_id
   GOOGLE_CLIENT_SECRET=your_google_client_secret
   JWT_SECRET=your_jwt_secret_key
   PSQL_USERNAME=your_db_username
   ```
4. Restart the application

### Issue: Database connection failed
**Solution**:
```bash
# Ensure PostgreSQL is running
brew services start postgresql

# Verify database exists
psql postgres -c "\l" | grep doorserve

# Create database if missing
psql postgres -c "CREATE DATABASE doorserve;"
```

### Issue: Permission denied for database
**Solution**:
```sql
-- Connect as superuser and grant permissions
psql postgres
GRANT ALL PRIVILEGES ON DATABASE doorserve TO akshit_kharbanda;
```

### Issue: Port 8080 already in use
**Solution**:
```bash
# Find process using port 8080
lsof -ti:8080

# Kill the process (replace PID with actual process ID)
kill -9 <PID>

# Or use a different port by adding to application.yml:
# server:
#   port: 8081
```

## 💳 Payment Integration

### Overview
The application includes a complete payment system integrated with Stripe that supports:
- Direct payment from service provider pages
- Cart-based checkout with multiple services
- Credit/Debit card payments
- Order tracking and payment history
- Automatic booking creation after successful payment

### Setup Requirements

#### 1. Stripe Account
1. Create a Stripe account at https://stripe.com
2. Get your API keys from the Stripe Dashboard
3. Add keys to environment files (see Payment Setup Guide)

#### 2. Environment Variables
Backend `.env`:
```bash
STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key_here
STRIPE_PUBLISHABLE_KEY=pk_test_your_stripe_publishable_key_here
```

Frontend `.env`:
```bash
REACT_APP_STRIPE_PUBLISHABLE_KEY=pk_test_your_stripe_publishable_key_here
```

#### 3. Dependencies
Backend dependencies are already included in `pom.xml`:
- Stripe Java SDK (version 24.16.0)

Frontend dependencies need to be installed:
```bash
cd door-serve-fe
npm install @stripe/react-stripe-js @stripe/stripe-js
```

### Database Schema
The payment system adds these tables:
- `payments` - Payment transaction details and Stripe integration
- `orders` - Groups services into orders with customer and payment links
- `order_items` - Individual services within orders with booking details

### API Endpoints
- `POST /api/payments/create-payment-intent` - Initialize payment
- `POST /api/payments/confirm-payment` - Confirm payment and create bookings
- `GET /api/payments/history` - Customer payment history
- `GET /api/payments/orders` - Customer orders

### Frontend Routes
- `/checkout` - Payment form and order summary
- `/payment-success` - Payment confirmation page
- `/orders` - Order and payment history

### Testing
Use Stripe test cards:
- Success: 4242 4242 4242 4242
- Decline: 4000 0000 0000 0002
- Authentication required: 4000 0025 0000 3155

For detailed setup instructions, see `PAYMENT_SETUP.md`.