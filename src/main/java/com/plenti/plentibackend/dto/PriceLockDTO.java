package com.plenti.plentibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for PriceLock entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceLockDTO {
    
    private Long id;
    private Long userId;
    private Long productId;
    private Double lockedPrice;
    private LocalDateTime expiryDate;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Explicit getters and setters for build compatibility
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public Double getLockedPrice() { return lockedPrice; }
    public void setLockedPrice(Double lockedPrice) { this.lockedPrice = lockedPrice; }
    
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
