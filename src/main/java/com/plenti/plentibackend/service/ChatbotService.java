package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.FAQDT;
import com.plenti.plentibackend.entity.ChatbotResponse;
import com.plenti.plentibackend.entity.FAQ;
import com.plenti.plentibackend.entity.Order;
import com.plenti.plentibackend.entity.OrderStatus;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.ChatbotResponseRepository;
import com.plenti.plentibackend.repository.FAQRepository;
import com.plenti.plentibackend.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for chatbot functionality with FAQ integration and order lookup
 */
@Service
@Slf4j
public class ChatbotService {

    @Autowired
    private FAQRepository faqRepository;
    
    @Autowired
    private ChatbotResponseRepository chatbotResponseRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Process a chatbot message and generate intelligent response
     */
    public Map<String, Object> processMessage(Long userId, String message) {
        Map<String, Object> response = new HashMap<>();
        
        // Trim and normalize message
        String normalizedMessage = message.trim().toLowerCase();
        
        // Try to find matching chatbot response from database
        List<ChatbotResponse> matchingResponses = chatbotResponseRepository
                .searchByKeyword(normalizedMessage);
        
        if (!matchingResponses.isEmpty()) {
            ChatbotResponse bestMatch = matchingResponses.get(0);
            response.put("message", bestMatch.getResponseText());
            
            // Parse and add suggestions if available
            if (bestMatch.getFollowUpSuggestions() != null) {
                response.put("suggestions", List.of(bestMatch.getFollowUpSuggestions().split(",")));
            }
            
            // Update usage count
            bestMatch.setUsageCount(bestMatch.getUsageCount() + 1);
            bestMatch.setLastUsedAt(LocalDateTime.now());
            chatbotResponseRepository.save(bestMatch);
            
            log.info("Chatbot response from database for user {}: {}", userId, bestMatch.getId());
            return response;
        }
        
        // Fallback to rule-based responses
        if (normalizedMessage.contains("order") && normalizedMessage.contains("track")) {
            response.put("message", "I can help you track your order! Please provide your order ID, or I can show you your recent orders.");
            response.put("suggestions", List.of("Show my orders", "Track order #", "Order status"));
            response.put("intent", "track_order");
        } else if (normalizedMessage.contains("order") || normalizedMessage.contains("delivery")) {
            response.put("message", "You can track your order in the Orders section. Need help with a specific order?");
            response.put("suggestions", List.of("Track my order", "Order status", "Delivery time", "Cancel order"));
        } else if (normalizedMessage.contains("payment") || normalizedMessage.contains("pay")) {
            response.put("message", "We accept cards, bank transfers, and wallet payments. What would you like to know?");
            response.put("suggestions", List.of("Payment methods", "Failed payment", "Refund status", "Payment help"));
        } else if (normalizedMessage.contains("promo") || normalizedMessage.contains("discount") || normalizedMessage.contains("coupon")) {
            response.put("message", "Check out our current promotions! I can also help you find the best deals.");
            response.put("suggestions", List.of("View promos", "Apply promo code", "Promo terms", "Active discounts"));
            response.put("intent", "promo_inquiry");
        } else if (normalizedMessage.contains("delivery") && normalizedMessage.contains("time")) {
            response.put("message", "We deliver within 60 minutes! Our standard delivery time is between 30-60 minutes from order confirmation.");
            response.put("suggestions", List.of("Track order", "Delivery areas", "Express delivery"));
        } else if (normalizedMessage.contains("refund") || normalizedMessage.contains("return")) {
            response.put("message", "You can request a refund for cancelled or undelivered orders. Refunds are processed within 5-7 business days.");
            response.put("suggestions", List.of("Request refund", "Refund policy", "Contact support"));
        } else if (normalizedMessage.contains("contact") || normalizedMessage.contains("support") || normalizedMessage.contains("help")) {
            response.put("message", "I'm here to help 24/7! You can also reach our support team via phone or email for urgent matters.");
            response.put("suggestions", List.of("Call support", "Email support", "FAQ", "Live chat"));
        } else {
            response.put("message", "I'm here to help! How can I assist you today?");
            response.put("suggestions", List.of("Track order", "Payment help", "Product info", "Promos", "Contact support"));
        }
        
        log.info("Chatbot rule-based response for user {}: {}", userId, normalizedMessage);
        return response;
    }
    
    /**
     * Get order status for a specific order
     */
    public Map<String, Object> getOrderStatus(Long userId, Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            throw new PlentiException("Order not found");
        }
        
        Order order = orderOpt.get();
        
        // Verify order belongs to user
        if (!order.getUserId().equals(userId)) {
            throw new PlentiException("Unauthorized access to order");
        }
        
        Map<String, Object> status = new HashMap<>();
        status.put("orderId", orderId);
        status.put("status", order.getStatus());
        status.put("statusMessage", getStatusMessage(order.getStatus()));
        status.put("orderDate", order.getOrderDate());
        status.put("estimatedDelivery", order.getEstimatedDelivery());
        status.put("totalAmount", order.getTotalAmount());
        
        if (order.getRiderName() != null) {
            status.put("riderName", order.getRiderName());
        }
        
        return status;
    }
    
    /**
     * Get user-friendly status message
     */
    private String getStatusMessage(OrderStatus status) {
        switch (status) {
            case PENDING:
                return "Your order has been received and is being processed";
            case PROCESSING:
                return "Your order is being prepared";
            case CONFIRMED:
                return "Your order has been confirmed";
            case PACKED:
                return "Your order is packed and ready for delivery";
            case OUT_FOR_DELIVERY:
                return "Your order is on the way!";
            case DELIVERED:
                return "Your order has been delivered";
            case CANCELLED:
                return "Your order was cancelled";
            case REFUNDED:
                return "Your order has been refunded";
            default:
                return "Order status updated";
        }
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
     * Search FAQs by keyword
     */
    public List<FAQDT> searchFAQs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getFAQs(null);
        }
        
        // This would require a search method in FAQRepository
        // For now, return all FAQs filtered by keyword
        return getFAQs(null).stream()
                .filter(faq -> 
                    faq.getQuestion().toLowerCase().contains(keyword.toLowerCase()) ||
                    faq.getAnswer().toLowerCase().contains(keyword.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    /**
     * Get personalized promo suggestions based on user behavior
     */
    public List<Map<String, String>> getPromoSuggestions(Long userId) {
        // In production, this would use ML-based recommendations
        // For now, return default suggestions
        return List.of(
                Map.of("code", "FIRST10", "description", "First Order Discount", "discount", "10%"),
                Map.of("code", "WEEKEND15", "description", "Weekend Special", "discount", "15%"),
                Map.of("code", "BULK20", "description", "Bulk Buy Offer", "discount", "20%"),
                Map.of("code", "SAVE50", "description", "Save ₦50 on orders above ₦1000", "discount", "₦50"),
                Map.of("code", "FREESHIP", "description", "Free Shipping", "discount", "Free Delivery")
        );
    }
    
    /**
     * Get FAQ categories
     */
    public List<String> getFAQCategories() {
        // This would require a method to get distinct categories
        return List.of("Orders", "Delivery", "Payment", "Products", "Account", "General");
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
