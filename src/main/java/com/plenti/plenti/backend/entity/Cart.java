package com.plenti.plenti.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;  // Links to User ID for the shopper

    @ElementCollection
    @CollectionTable(name = "cart_items", joinColumns = @JoinColumn(name = "cart_id"))
    @MapKeyColumn(name = "product_id")  // Key: Product ID
    @Column(name = "quantity")         // Value: Number of items
    private Map<Long, Integer> items = new HashMap<>();  // Cart items as product ID to quantity

    @Column(name = "total")
    private double total;  // Total price of cart
}