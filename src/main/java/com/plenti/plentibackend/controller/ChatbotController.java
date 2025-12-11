package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.FAQDT;
import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.service.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for chatbot operations
 */
@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "Chatbot", description = "Chatbot and FAQ endpoints")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/message")
    @Operation(summary = "Send message to chatbot", description = "Process a user message and get chatbot response")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> sendMessage(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String message = request.get("message").toString();
        
        Map<String, Object> response = chatbotService.processMessage(userId, message);
        return ResponseEntity.ok(ResponseDTO.success(response));
    }

    @GetMapping("/faqs")
    @Operation(summary = "Get FAQs", description = "Get frequently asked questions, optionally filtered by category")
    public ResponseEntity<ResponseDTO<List<FAQDT>>> getFAQs(
            @RequestParam(required = false) String category) {
        List<FAQDT> faqs = chatbotService.getFAQs(category);
        return ResponseEntity.ok(ResponseDTO.success(faqs));
    }

    @GetMapping("/promos")
    @Operation(summary = "Get promo suggestions", description = "Get personalized promo code suggestions for user")
    public ResponseEntity<ResponseDTO<List<Map<String, String>>>> getPromos(
            @RequestParam Long userId) {
        List<Map<String, String>> promos = chatbotService.getPromoSuggestions(userId);
        return ResponseEntity.ok(ResponseDTO.success(promos));
    }

    @PostMapping("/escalate")
    @Operation(summary = "Escalate to support", description = "Escalate chatbot conversation to human support")
    public ResponseEntity<ResponseDTO<Map<String, String>>> escalateToSupport(
            @RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String reason = request.getOrDefault("reason", "User requested support").toString();
        
        // In production, this would create a support ticket
        Map<String, String> response = Map.of(
                "status", "escalated",
                "message", "Your conversation has been escalated to our support team. They will contact you shortly.",
                "ticketId", "TICKET_" + System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(ResponseDTO.success("Escalated to support", response));
    }
    
    @GetMapping("/order-status/{orderId}")
    @Operation(summary = "Get order status", description = "Get order status through chatbot")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Long userId) {
        Map<String, Object> status = chatbotService.getOrderStatus(userId, orderId);
        return ResponseEntity.ok(ResponseDTO.success(status));
    }
    
    @GetMapping("/faqs/search")
    @Operation(summary = "Search FAQs", description = "Search FAQs by keyword")
    public ResponseEntity<ResponseDTO<List<FAQDT>>> searchFAQs(
            @RequestParam String keyword) {
        List<FAQDT> faqs = chatbotService.searchFAQs(keyword);
        return ResponseEntity.ok(ResponseDTO.success(faqs));
    }
}
