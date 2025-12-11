package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a user's loyalty points balance
 */
@Entity
@Table(name = "plenti_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlentiPoints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private Integer balance = 0;

    @Column(nullable = false)
    private Integer totalEarned = 0;

    @Column(nullable = false)
    private Integer totalRedeemed = 0;

    @Column(nullable = false)
    private String tier = "BRONZE"; // BRONZE, SILVER, GOLD, PLATINUM

    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
        if (balance == null) {
            balance = 0;
        }
        if (totalEarned == null) {
            totalEarned = 0;
        }
        if (totalRedeemed == null) {
            totalRedeemed = 0;
        }
        if (tier == null) {
            tier = "BRONZE";
        }
    }

    // Explicit getters and setters for build compatibility
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Integer getBalance() { return balance; }
    public void setBalance(Integer balance) { this.balance = balance; }
    
    public Integer getTotalEarned() { return totalEarned; }
    public void setTotalEarned(Integer totalEarned) { this.totalEarned = totalEarned; }
    
    public Integer getTotalRedeemed() { return totalRedeemed; }
    public void setTotalRedeemed(Integer totalRedeemed) { this.totalRedeemed = totalRedeemed; }
    
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
