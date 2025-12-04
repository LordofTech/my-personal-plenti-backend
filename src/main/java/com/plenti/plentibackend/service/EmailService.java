package com.plenti.plentibackend.service;

import org.springframework.stereotype.Service;

/**
 * Service for email operations (placeholder implementation)
 */
@Service
public class EmailService {

    public void sendWelcomeEmail(String email, String name) {
        // Placeholder for email sending
        System.out.println("Sending welcome email to: " + email);
    }

    public void sendOrderConfirmationEmail(String email, Long orderId) {
        // Placeholder for email sending
        System.out.println("Sending order confirmation email to: " + email + " for order: " + orderId);
    }

    public void sendOrderStatusEmail(String email, Long orderId, String status) {
        // Placeholder for email sending
        System.out.println("Sending order status email to: " + email + " for order: " + orderId + " - Status: " + status);
    }
}
