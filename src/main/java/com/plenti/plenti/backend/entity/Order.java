package com.plenti.plenti.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;  // Updated to Long to match User ID type

    @ElementCollection
    @CollectionTable(name = "order_product_ids", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "product_id")
    private List<String> productIds = new ArrayList<>();  // List of product IDs as strings (or change to Long if needed)

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "status")
    private String status;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "tracking_url")
    private String trackingUrl;
}