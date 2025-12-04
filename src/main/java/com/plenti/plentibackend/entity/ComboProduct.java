package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing product bundles/combo deals
 */
@Entity
@Table(name = "combo_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComboProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "combo_product_items", 
                    joinColumns = @JoinColumn(name = "combo_id"))
    @Column(name = "product_id")
    private List<Long> productIds = new ArrayList<>();

    @Column(nullable = false)
    private Double originalPrice;

    @Column(nullable = false)
    private Double comboPrice;

    @Column(nullable = false)
    private Boolean isActive = true;

    private String imageUrl;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }
}
