# Payment Mode Configuration

This document explains how to switch between mock and real payment modes.

## Current Configuration

**Backend**: `door-serve/src/main/java/com/doorserve/service/PaymentService.java`
```java
// Toggle this to switch between mock and real payments
private final boolean mockPayment = true;
```

**Frontend**: `door-serve-fe/src/config/payment.ts`
```typescript
export const paymentConfig = {
  // Toggle this to switch between mock and real payments
  mockPayment: true,
  
  // Stripe configuration
  stripePublishableKey: process.env.REACT_APP_STRIPE_PUBLISHABLE_KEY || '',
};
```

## Switching to Real Payments

### Step 1: Set Up Stripe Keys
1. Create a Stripe account at https://stripe.com
2. Get your API keys from Developers → API Keys
3. Update environment files:

**Backend** (`door-serve/.env`):
```env
STRIPE_SECRET_KEY=sk_test_your_actual_stripe_secret_key
STRIPE_PUBLISHABLE_KEY=pk_test_your_actual_stripe_publishable_key
```

**Frontend** (`door-serve-fe/.env`):
```env
REACT_APP_STRIPE_PUBLISHABLE_KEY=pk_test_your_actual_stripe_publishable_key
```

### Step 2: Change Configuration Flags
**Backend** - Change in `PaymentService.java`:
```java
private final boolean mockPayment = false; // Changed from true to false
```

**Frontend** - Change in `payment.ts`:
```typescript
mockPayment: false, // Changed from true to false
```

### Step 3: Restart Applications
1. Restart backend server
2. Restart frontend development server

## Testing

### Mock Mode (Current)
- No Stripe account needed
- Simulated payments
- Full flow testing
- Database integration
- Demo UI indicators

### Real Mode
- Requires Stripe account
- Real payment processing
- Use Stripe test cards:
  - Success: `4242 4242 4242 4242`
  - Declined: `4000 0000 0000 0002`
  - Authentication: `4000 0025 0000 3155`

## Benefits of This Approach

✅ **Single Point of Control**: Change one boolean in each codebase
✅ **Easy Switching**: No code changes needed, just configuration
✅ **Development Friendly**: Mock mode for development, real mode for production
✅ **Clear Separation**: Mock and real payment logic are clearly separated
✅ **Environment Independent**: Works regardless of Stripe key configuration