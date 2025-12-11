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

    // Explicit getters and setters for build compatibility
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getInventoryCapacity() { return inventoryCapacity; }
    public void setInventoryCapacity(Integer inventoryCapacity) { this.inventoryCapacity = inventoryCapacity; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
