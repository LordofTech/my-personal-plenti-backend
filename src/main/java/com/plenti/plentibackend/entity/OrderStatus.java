package com.plenti.plentibackend.entity;

/**
 * Enum representing different order statuses in the order lifecycle
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PACKED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    REFUNDED
}
