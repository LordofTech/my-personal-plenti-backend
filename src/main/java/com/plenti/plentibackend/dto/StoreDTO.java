package com.plenti.plentibackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Store entity (extended)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreDTO {

    private Long id;

    @NotBlank(message = "Store name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location; // street/address

    @NotBlank(message = "Type is required")
    private String type; // "dark" or "partner"

    @NotNull(message = "Inventory capacity is required")
    private Integer inventoryCapacity;

    private Double latitude;
    private Double longitude;

    // Extra fields you asked for
    private String city; // New field
    private String state; // New field
    private String phone; // New field
    private String email; // New field

    // Assuming operating hours as a string, but you can also structure it better with start and end times
    private String operatingHours; // New field

    private Boolean active; // New field
}
