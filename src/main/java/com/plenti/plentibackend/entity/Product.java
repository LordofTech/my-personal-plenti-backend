package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a product in the Plenti catalog
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double price;

    private String category;

    @Column(nullable = false)
    private Integer stock = 0;

    private String imageUrl;

    private LocalDateTime lastUpdated = LocalDateTime.now();

    private Long categoryId;

    private Double averageRating = 0.0;

    private Integer reviewCount = 0;

    private Double bulkPrice;

    @Column(nullable = false)
    private Boolean isClearance = false;

    @Column(nullable = false)
    private Boolean isFreebie = false;

    @Column(nullable = false)
    private Boolean isFeatured = false;

    private Double flashSalePrice;

    private LocalDateTime flashSaleEnd;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
        if (stock == null) {
            stock = 0;
        }
        if (averageRating == null) {
            averageRating = 0.0;
        }
        if (reviewCount == null) {
            reviewCount = 0;
        }
        if (isClearance == null) {
            isClearance = false;
        }
        if (isFreebie == null) {
            isFreebie = false;
        }
        if (isFeatured == null) {
            isFeatured = false;
        }
    }
}
