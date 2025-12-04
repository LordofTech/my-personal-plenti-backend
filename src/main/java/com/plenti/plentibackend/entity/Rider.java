package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a delivery rider
 */
@Entity
@Table(name = "riders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    private String email;

    @Column(nullable = false)
    private String vehicleType; // BIKE, CAR, VAN

    private String vehicleNumber;

    @Column(nullable = false)
    private String status = "AVAILABLE"; // AVAILABLE, BUSY, OFFLINE

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Double rating = 5.0;

    @Column(nullable = false)
    private Integer totalDeliveries = 0;

    private Double currentLatitude;

    private Double currentLongitude;

    private LocalDateTime lastLocationUpdate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null || status.isEmpty()) {
            status = "AVAILABLE";
        }
        if (isActive == null) {
            isActive = true;
        }
        if (rating == null) {
            rating = 5.0;
        }
        if (totalDeliveries == null) {
            totalDeliveries = 0;
        }
    }
}
