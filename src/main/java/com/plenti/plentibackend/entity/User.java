package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity representing a user in the Plenti system
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(unique = true)
    private String referralCode;

    @Column(nullable = false)
    private Double metaCoins = 0.0;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_payment_methods", 
                    joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "payment_type")
    @Column(name = "payment_details")
    private Map<String, String> paymentMethods = new HashMap<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (metaCoins == null) {
            metaCoins = 0.0;
        }
    }
}
