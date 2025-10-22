package com.doorserve.service;

import com.doorserve.dto.CheckoutItem;
import com.doorserve.dto.PaymentConfirmationRequest;
import com.doorserve.dto.PaymentIntentRequest;
import com.doorserve.dto.PaymentIntentResponse;
import com.doorserve.entity.Order;
import com.doorserve.entity.OrderItem;
import com.doorserve.entity.Payment;
import com.doorserve.model.Booking;
import com.doorserve.model.PartnerService;
import com.doorserve.model.User;
import com.doorserve.repository.BookingRepository;
import com.doorserve.repository.CartRepository;
import com.doorserve.repository.OrderItemRepository;
import com.doorserve.repository.OrderRepository;
import com.doorserve.repository.PartnerServiceRepository;
import com.doorserve.repository.PaymentRepository;
import com.doorserve.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final PartnerServiceRepository partnerServiceRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Value("${STRIPE_SECRET_KEY:sk_test_placeholder}")
    private String stripeSecretKey;

    // Toggle this to switch between mock and real payments
    private final boolean mockPayment = true;

    private void validateStripeConfiguration() {
        if (stripeSecretKey == null || stripeSecretKey.equals("sk_test_placeholder") ||
                stripeSecretKey.equals("sk_test_your_stripe_secret_key_here")) {
            throw new IllegalStateException(
                    "Stripe secret key is not properly configured. Please set STRIPE_SECRET_KEY environment variable with your actual Stripe secret key.");
        }
    }

    @Transactional
    public PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request, Long customerId)
            throws StripeException {
        if (mockPayment) {
            return createMockPaymentIntent(request, customerId);
        }

        validateStripeConfiguration();
        Stripe.apiKey = stripeSecretKey;

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Create order first
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(request.getAmount());
        order.setStatus("pending");
        order = orderRepository.save(order);

        // Create order items
        List<OrderItem> orderItems = new ArrayList<>();

        if ("cart".equals(request.getPaymentType())) {
            // Get items from cart
            List<com.doorserve.model.Cart> cartItems = cartRepository.findByUser(customer);
            for (com.doorserve.model.Cart cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setPartnerService(cartItem.getPartnerService());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());
                orderItems.add(orderItem);
            }
        } else if ("direct".equals(request.getPaymentType()) && request.getItems() != null) {
            // Direct payment with provided items
            for (CheckoutItem item : request.getItems()) {
                PartnerService partnerService = partnerServiceRepository.findById(item.getPartnerServiceId())
                        .orElseThrow(() -> new RuntimeException("Service not found"));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setPartnerService(partnerService);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getPrice());
                orderItem.setBookingDate(item.getBookingDate());
                orderItem.setStartTime(item.getStartTime());
                orderItem.setEndTime(item.getEndTime());
                orderItems.add(orderItem);
            }
        }

        orderItemRepository.saveAll(orderItems);

        // Create Stripe PaymentIntent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount().multiply(new BigDecimal("100")).longValue()) // Convert to cents
                .setCurrency(request.getCurrency().toLowerCase())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Save payment record
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setStripePaymentIntentId(paymentIntent.getId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus("pending");
        paymentRepository.save(payment);

        // Update order with payment
        order.setPayment(payment);
        orderRepository.save(order);

        return new PaymentIntentResponse(
                paymentIntent.getClientSecret(),
                paymentIntent.getId(),
                order.getId());
    }

    @Transactional
    public void confirmPayment(PaymentConfirmationRequest request, Long customerId) throws StripeException {
        if (mockPayment) {
            confirmMockPayment(request, customerId);
            return;
        }

        validateStripeConfiguration();
        Stripe.apiKey = stripeSecretKey;

        // Retrieve payment intent from Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(request.getPaymentIntentId());

        // Update payment status
        Payment payment = paymentRepository.findByStripePaymentIntentId(request.getPaymentIntentId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(paymentIntent.getStatus());
        payment.setPaymentMethod(paymentIntent.getPaymentMethod());
        paymentRepository.save(payment);

        // Update order status
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("succeeded".equals(paymentIntent.getStatus())) {
            order.setStatus("confirmed");
            orderRepository.save(order);

            // Create bookings for each order item
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            for (OrderItem item : orderItems) {
                if (item.getBookingDate() != null && item.getStartTime() != null && item.getEndTime() != null) {
                    Booking booking = new Booking();
                    booking.setCustomer(order.getCustomer());
                    booking.setPartner(item.getPartnerService().getPartner());
                    booking.setPartnerService(item.getPartnerService());
                    booking.setBookingDate(item.getBookingDate());
                    booking.setStartTime(item.getStartTime());
                    booking.setEndTime(item.getEndTime());
                    booking.setPrice(item.getPrice());
                    booking.setTotalPrice(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                    booking.setStatus(com.doorserve.model.BookingStatus.CONFIRMED);
                    bookingRepository.save(booking);

                    // Update payment with booking reference
                    payment.setBooking(booking);
                    paymentRepository.save(payment);
                }
            }

            // Clear cart if payment was from cart
            cartRepository.deleteByUser(order.getCustomer());
        } else {
            order.setStatus("failed");
            orderRepository.save(order);
        }
    }

    // Mock payment method for testing without real Stripe keys
    private PaymentIntentResponse createMockPaymentIntent(PaymentIntentRequest request, Long customerId) {
        System.out.println("🧪 MOCK MODE: Creating mock payment intent for testing");

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(request.getAmount());
        order.setStatus("pending");
        order = orderRepository.save(order);

        // Create order items
        List<OrderItem> orderItems = new ArrayList<>();

        if ("cart".equals(request.getPaymentType())) {
            List<com.doorserve.model.Cart> cartItems = cartRepository.findByUser(customer);
            for (com.doorserve.model.Cart cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setPartnerService(cartItem.getPartnerService());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());
                orderItems.add(orderItem);
            }
        } else if ("direct".equals(request.getPaymentType()) && request.getItems() != null) {
            for (CheckoutItem item : request.getItems()) {
                PartnerService partnerService = partnerServiceRepository.findById(item.getPartnerServiceId())
                        .orElseThrow(() -> new RuntimeException("Service not found"));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setPartnerService(partnerService);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getPrice());
                orderItem.setBookingDate(item.getBookingDate());
                orderItem.setStartTime(item.getStartTime());
                orderItem.setEndTime(item.getEndTime());
                orderItems.add(orderItem);
            }
        }

        orderItemRepository.saveAll(orderItems);

        // Create mock payment record with unique ID
        String uniquePaymentId = "pi_mock_" + UUID.randomUUID().toString().replace("-", "");
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setStripePaymentIntentId(uniquePaymentId);
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus("requires_payment_method");
        paymentRepository.save(payment);

        order.setPayment(payment);
        orderRepository.save(order);

        // Return mock response
        return new PaymentIntentResponse(
                "pi_mock_client_secret_" + UUID.randomUUID().toString().replace("-", ""),
                payment.getStripePaymentIntentId(),
                order.getId());
    }

    // Mock payment confirmation for testing
    private void confirmMockPayment(PaymentConfirmationRequest request, Long customerId) {
        System.out.println("🧪 MOCK MODE: Confirming mock payment");

        Payment payment = paymentRepository.findByStripePaymentIntentId(request.getPaymentIntentId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus("succeeded");
        payment.setPaymentMethod("card");
        paymentRepository.save(payment);

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus("confirmed");
        orderRepository.save(order);

        // Create mock bookings
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            if (item.getBookingDate() != null && item.getStartTime() != null && item.getEndTime() != null) {
                Booking booking = new Booking();
                booking.setCustomer(order.getCustomer());
                booking.setPartner(item.getPartnerService().getPartner());
                booking.setPartnerService(item.getPartnerService());
                booking.setBookingDate(item.getBookingDate());
                booking.setStartTime(item.getStartTime());
                booking.setEndTime(item.getEndTime());
                booking.setPrice(item.getPrice());
                booking.setTotalPrice(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                booking.setStatus(com.doorserve.model.BookingStatus.CONFIRMED);
                bookingRepository.save(booking);

                payment.setBooking(booking);
                paymentRepository.save(payment);
            }
        }

        // Clear cart
        cartRepository.deleteByUser(order.getCustomer());

        System.out.println("✅ MOCK MODE: Payment confirmed successfully");
    }
}