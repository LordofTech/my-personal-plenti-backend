package com.plenti.plentibackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Store entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreDTO {
    
    private Long id;
    
    @NotBlank(message = "Store name is required")
    private String name;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotNull(message = "Inventory capacity is required")
    private Integer inventoryCapacity;
    
    private Double latitude;
    private Double longitude;
}
