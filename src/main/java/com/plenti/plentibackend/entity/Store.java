package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a physical store (dark store or partner store)
 */
@Entity
@Table(name = "stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String type; // "dark" or "partner"

    @Column(nullable = false)
    private Integer inventoryCapacity;

    private Double latitude;
    private Double longitude;

    // New optional fields
    private String city; // New field
    private String state; // New field
    private String phone; // New field
    private String email; // New field
    private String operatingHours; // New field
    private Boolean active = true; // New field, default to true
}
