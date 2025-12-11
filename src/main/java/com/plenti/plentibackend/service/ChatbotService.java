package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.FAQDT;
import com.plenti.plentibackend.entity.FAQ;
import com.plenti.plentibackend.repository.FAQRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for chatbot functionality
 */
@Service
@Slf4j
public class ChatbotService {

    @Autowired
    private FAQRepository faqRepository;

    /**
     * Process a chatbot message and generate response
     */
    public Map<String, Object> processMessage(Long userId, String message) {
        Map<String, Object> response = new HashMap<>();
        
        // Simple keyword-based response
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("order") || lowerMessage.contains("delivery")) {
            response.put("message", "You can track your order in the Orders section. Need help with a specific order?");
            response.put("suggestions", List.of("Track my order", "Order status", "Delivery time"));
        } else if (lowerMessage.contains("payment") || lowerMessage.contains("pay")) {
            response.put("message", "We accept cards, bank transfers, and wallet payments. What would you like to know?");
            response.put("suggestions", List.of("Payment methods", "Failed payment", "Refund status"));
        } else if (lowerMessage.contains("promo") || lowerMessage.contains("discount")) {
            response.put("message", "Check out our current promotions in the Deals section!");
            response.put("suggestions", List.of("View promos", "Apply promo code", "Promo terms"));
        } else {
            response.put("message", "I'm here to help! How can I assist you today?");
            response.put("suggestions", List.of("Track order", "Payment help", "Product info", "Contact support"));
        }
        
        log.info("Chatbot message processed for user {}: {}", userId, message);
        return response;
    }

    /**
     * Get FAQs by category
     */
    public List<FAQDT> getFAQs(String category) {
        List<FAQ> faqs;
        if (category != null && !category.isEmpty()) {
            faqs = faqRepository.findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(category);
        } else {
            faqs = faqRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        }
        
        return faqs.stream()
                .map(this::toFAQDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get personalized promo suggestions
     */
    public List<Map<String, String>> getPromoSuggestions(Long userId) {
        // Placeholder for ML-based recommendations
        return List.of(
                Map.of("title", "First Order Discount", "code", "FIRST10", "discount", "10%"),
                Map.of("title", "Weekend Special", "code", "WEEKEND15", "discount", "15%"),
                Map.of("title", "Bulk Buy Offer", "code", "BULK20", "discount", "20%")
        );
    }

    private FAQDT toFAQDTO(FAQ faq) {
        FAQDT dto = new FAQDT();
        dto.setId(faq.getId());
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        dto.setCategory(faq.getCategory());
        dto.setDisplayOrder(faq.getDisplayOrder());
        dto.setIsActive(faq.getIsActive());
        dto.setCreatedAt(faq.getCreatedAt());
        dto.setUpdatedAt(faq.getUpdatedAt());
        return dto;
    }
}
