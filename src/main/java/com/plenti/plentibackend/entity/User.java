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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private Boolean suspended = false;

    @Column(nullable = false)
    private Double trustScore = 100.0;

    @Column(nullable = false)
    private Boolean isGuest = false;

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
        if (role == null) {
            role = Role.USER;
        }
        if (suspended == null) {
            suspended = false;
        }
        if (trustScore == null) {
            trustScore = 100.0;
        }
        if (isGuest == null) {
            isGuest = false;
        }
    }
}
