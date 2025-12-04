package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing activity logs for audit trail
 */
@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String action; // USER_CREATED, PRODUCT_UPDATED, ORDER_CANCELLED, etc.

    @Column(length = 1000)
    private String details;

    private String entityType; // USER, PRODUCT, ORDER, etc.

    private Long entityId;

    private String ipAddress;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
