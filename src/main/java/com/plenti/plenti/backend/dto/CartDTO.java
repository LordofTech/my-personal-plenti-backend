package com.plenti.plenti.backend.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CartDTO {
    private String id;
    private String userId;
    private Map<String, Integer> items; // productId: quantity
    private double total;
}
