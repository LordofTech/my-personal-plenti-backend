package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing an OTP verification code
 */
@Entity
@Table(name = "otps", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"phoneNumber", "type"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Otp {

    /**
     * Enum for OTP types
     */
    public enum OtpType {
        REGISTRATION,
        PASSWORD_RESET
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String otpCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpType type = OtpType.REGISTRATION;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean used = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (used == null) {
            used = false;
        }
        if (type == null) {
            type = OtpType.REGISTRATION;
        }
    }
}
