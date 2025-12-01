package com.plenti.plenti.backend.dto;

import lombok.Data;

@Data
public class StoreDTO {
    private String id;
    private String name;
    private String location;
    private String type; // "dark" or "partner"
    private int inventoryCapacity;
}
