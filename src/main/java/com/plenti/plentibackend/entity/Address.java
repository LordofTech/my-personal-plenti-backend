package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a delivery address
 */
@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String label; // Home, Office, Other

    @Column(nullable = false)
    private String streetAddress;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country = "Nigeria";

    private String postalCode;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private Boolean isDefault = false;

    @PrePersist
    protected void onCreate() {
        if (isDefault == null) {
            isDefault = false;
        }
        if (country == null) {
            country = "Nigeria";
        }
    }
}
