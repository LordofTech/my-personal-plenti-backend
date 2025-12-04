package com.plenti.plentibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for analytics and dashboard data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
    
    private Long totalOrders;
    private Double totalRevenue;
    private Double averageOrderValue;
    private Long totalUsers;
    private Long totalProducts;
    private List<Map<String, Object>> topProducts;
    private Map<String, Long> ordersByStatus;
}
