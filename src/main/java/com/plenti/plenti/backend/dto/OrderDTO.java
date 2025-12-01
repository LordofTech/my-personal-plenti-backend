package com.plenti.plenti.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private String id;
    private String userId;
    private List<String> productIds;
    private double totalAmount;
    private String status; // e.g., "Placed", "On the way"
    private LocalDateTime orderDate;
    private String trackingUrl; // Google Maps link
}
