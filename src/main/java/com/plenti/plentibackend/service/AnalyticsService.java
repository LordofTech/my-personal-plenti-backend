package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.AnalyticsDTO;
import com.plenti.plentibackend.entity.Order;
import com.plenti.plentibackend.entity.OrderStatus;
import com.plenti.plentibackend.entity.Product;
import com.plenti.plentibackend.repository.OrderRepository;
import com.plenti.plentibackend.repository.ProductRepository;
import com.plenti.plentibackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for analytics and reporting
 */
@Service
public class AnalyticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public AnalyticsDTO getAnalyticsSummary() {
        AnalyticsDTO analytics = new AnalyticsDTO();

        List<Order> orders = orderRepository.findAll();
        
        analytics.setTotalOrders((long) orders.size());
        analytics.setTotalUsers(userRepository.count());
        analytics.setTotalProducts(productRepository.count());

        // Calculate total revenue
        double totalRevenue = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Order::getTotalAmount)
                .sum();
        analytics.setTotalRevenue(totalRevenue);

        // Calculate average order value
        double avgOrderValue = orders.isEmpty() ? 0 : totalRevenue / orders.size();
        analytics.setAverageOrderValue(avgOrderValue);

        // Orders by status
        Map<String, Long> ordersByStatus = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getStatus().toString(),
                        Collectors.counting()
                ));
        analytics.setOrdersByStatus(ordersByStatus);

        // Top products
        analytics.setTopProducts(getTopProducts());

        return analytics;
    }

    private List<Map<String, Object>> getTopProducts() {
        List<Product> products = productRepository.findAll();
        
        return products.stream()
                .sorted(Comparator.comparingDouble(Product::getAverageRating).reversed())
                .limit(10)
                .map(product -> {
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("id", product.getId());
                    productMap.put("name", product.getName());
                    productMap.put("price", product.getPrice());
                    productMap.put("rating", product.getAverageRating());
                    productMap.put("reviews", product.getReviewCount());
                    return productMap;
                })
                .collect(Collectors.toList());
    }
}
