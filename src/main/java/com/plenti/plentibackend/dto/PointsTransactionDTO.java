package com.plenti.plentibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for PointsTransaction entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointsTransactionDTO {
    
    private Long id;
    private Long userId;
    private Integer points;
    private String type;
    private String reason;
    private LocalDateTime createdAt;
    private Long relatedOrderId;

    // Explicit getters and setters for build compatibility
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Long getRelatedOrderId() { return relatedOrderId; }
    public void setRelatedOrderId(Long relatedOrderId) { this.relatedOrderId = relatedOrderId; }
}
