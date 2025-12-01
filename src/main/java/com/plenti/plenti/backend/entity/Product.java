package com.plenti.plenti.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private double price;
    @Column(name = "category")
    private String category;
    @Column(name = "stock")
    private int stock;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}