package com.plenti.plentibackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for PromoCode entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeDTO {
    
    private Long id;
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Discount type is required")
    private String discountType;
    
    @NotNull(message = "Discount value is required")
    private Double discountValue;
    
    private Double minOrderAmount;
    private Integer maxUses;
    private Integer usedCount;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Boolean active;
}
