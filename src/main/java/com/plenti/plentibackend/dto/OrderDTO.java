package com.plenti.plentibackend.dto;

import com.plenti.plentibackend.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Order entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private List<String> productIds;
    
    @NotNull(message = "Total amount is required")
    private Double totalAmount;
    
    private OrderStatus status;
    private LocalDateTime orderDate;
    private String trackingUrl;
    private String deliveryAddress;
    private String riderId;
    private String riderName;
    private LocalDateTime estimatedDelivery;
    private String promoCodeApplied;
    private Double discountAmount;
}
