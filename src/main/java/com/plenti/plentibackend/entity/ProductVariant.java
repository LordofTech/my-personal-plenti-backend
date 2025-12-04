package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a product variant (size, color, etc.)
 */
@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String variantType; // SIZE, COLOR, etc.

    @Column(nullable = false)
    private String variantValue; // S, M, L, XL, Red, Blue, etc.

    private Double priceAdjustment = 0.0;

    @Column(nullable = false)
    private Integer stock = 0;

    private String sku;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (stock == null) {
            stock = 0;
        }
        if (priceAdjustment == null) {
            priceAdjustment = 0.0;
        }
    }
}
