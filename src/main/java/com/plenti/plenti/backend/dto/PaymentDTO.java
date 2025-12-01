package com.plenti.plenti.backend.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private String id;
    private String orderId;
    private double amount;
    private String status;
    private String reference;
}
