package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.entity.SupportTicket;
import com.plenti.plentibackend.service.SupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for support and engagement operations
 */
@RestController
@RequestMapping("/api/support")
@Tag(name = "Support", description = "Support and customer engagement endpoints")
public class SupportController {

    @Autowired(required = false)
    private SupportTicketService supportTicketService;

    @GetMapping("/faq")
    @Operation(summary = "Get FAQs", description = "Get frequently asked questions")
    public ResponseEntity<ResponseDTO<List<Map<String, String>>>> getFAQs() {
        List<Map<String, String>> faqs = List.of(
            Map.of(
                "question", "What is Plenti's delivery time?",
                "answer", "We deliver within 60 minutes in most areas."
            ),
            Map.of(
                "question", "How do I track my order?",
                "answer", "You can track your order using the tracking URL sent to you after placing the order."
            ),
            Map.of(
                "question", "What payment methods are accepted?",
                "answer", "We accept card payments, bank transfers, and virtual accounts via Monnify."
            ),
            Map.of(
                "question", "Can I cancel my order?",
                "answer", "Yes, you can cancel your order before it's out for delivery."
            ),
            Map.of(
                "question", "How do I use a promo code?",
                "answer", "Enter your promo code at checkout to apply discounts to your order."
            )
        );
        return ResponseEntity.ok(ResponseDTO.success(faqs));
    }

    @PostMapping("/feedback")
    @Operation(summary = "Submit feedback", description = "Submit customer feedback")
    public ResponseEntity<ResponseDTO<String>> submitFeedback(@RequestBody Map<String, String> feedback) {
        // In a real application, save feedback to database
        String message = feedback.get("message");
        String userId = feedback.getOrDefault("userId", "anonymous");
        System.out.println("Feedback from user " + userId + ": " + message);
        return ResponseEntity.ok(ResponseDTO.success("Thank you for your feedback!", "received"));
    }

    @PostMapping("/rate")
    @Operation(summary = "Rate delivery", description = "Rate delivery experience")
    public ResponseEntity<ResponseDTO<String>> rateDelivery(@RequestBody Map<String, Object> rating) {
        // In a real application, save rating to database
        Long orderId = Long.valueOf(rating.get("orderId").toString());
        Integer stars = Integer.valueOf(rating.get("stars").toString());
        String comment = rating.getOrDefault("comment", "").toString();
        
        System.out.println("Delivery rating for order " + orderId + ": " + stars + " stars - " + comment);
        return ResponseEntity.ok(ResponseDTO.success("Thank you for rating our service!", "rated"));
    }

    @GetMapping("/help")
    @Operation(summary = "Get help topics", description = "Get customer help topics")
    public ResponseEntity<ResponseDTO<Map<String, List<String>>>> getHelpTopics() {
        Map<String, List<String>> topics = new HashMap<>();
        topics.put("Orders", List.of("How to place an order", "Tracking orders", "Cancelling orders"));
        topics.put("Payments", List.of("Payment methods", "Payment issues", "Refunds"));
        topics.put("Account", List.of("Creating an account", "Password reset", "Referral program"));
        topics.put("Delivery", List.of("Delivery times", "Delivery areas", "Contactless delivery"));
        
        return ResponseEntity.ok(ResponseDTO.success(topics));
    }

    @PostMapping("/tickets")
    @Operation(summary = "Create support ticket")
    public ResponseEntity<SupportTicket> createTicket(@RequestBody SupportTicket ticket) {
        if (supportTicketService == null) {
            throw new RuntimeException("Support ticket service not available");
        }
        return ResponseEntity.ok(supportTicketService.createTicket(ticket));
    }

    @GetMapping("/tickets/{userId}")
    @Operation(summary = "Get user's support tickets")
    public ResponseEntity<List<SupportTicket>> getUserTickets(@PathVariable Long userId) {
        if (supportTicketService == null) {
            throw new RuntimeException("Support ticket service not available");
        }
        return ResponseEntity.ok(supportTicketService.getUserTickets(userId));
    }

    @GetMapping("/tickets/status/{status}")
    @Operation(summary = "Get tickets by status (admin)")
    public ResponseEntity<List<SupportTicket>> getTicketsByStatus(@PathVariable String status) {
        if (supportTicketService == null) {
            throw new RuntimeException("Support ticket service not available");
        }
        return ResponseEntity.ok(supportTicketService.getTicketsByStatus(status));
    }

    @PutMapping("/tickets/{id}/status")
    @Operation(summary = "Update ticket status (admin)")
    public ResponseEntity<SupportTicket> updateTicketStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        if (supportTicketService == null) {
            throw new RuntimeException("Support ticket service not available");
        }
        String status = statusUpdate.get("status");
        String resolution = statusUpdate.get("resolution");
        return ResponseEntity.ok(supportTicketService.updateTicketStatus(id, status, resolution));
    }
}
