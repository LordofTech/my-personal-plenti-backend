package com.plenti.plentibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Wishlist entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDTO {
    
    private Long id;
    private Long userId;
    private List<Long> productIds;
    private LocalDateTime createdAt;
}
