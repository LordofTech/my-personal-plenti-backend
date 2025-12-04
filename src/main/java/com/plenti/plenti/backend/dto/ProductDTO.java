package com.plenti.plenti.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private int stock;
    private String imageUrl;
    private LocalDateTime lastUpdated;
}
