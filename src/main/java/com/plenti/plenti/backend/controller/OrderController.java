package com.plenti.plenti.backend.controller;

import com.plenti.plenti.backend.dto.OrderDTO;
import com.plenti.plenti.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "Place a new order", description = "Places an order from the cart in the Plenti ecommerce app, supporting 60-minute delivery or free with reliable availability.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order data")
    })
    public ResponseEntity<OrderDTO> placeOrder(
            @Parameter(description = "Order details including userId, deliveryAddress, paymentMethod") @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.placeOrder(orderDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Track a specific order", description = "Tracks an order by ID in the Plenti app, providing status, ETA (e.g., 16 min), and rider info for fast delivery.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order details retrieved"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "Order ID") @PathVariable String id) {
        return ResponseEntity.ok(orderService.trackOrder(id));
    }

    @GetMapping
    @Operation(summary = "Get order history", description = "Retrieves the list of orders for a user in the Plenti app, matching order history screens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order history retrieved"),
            @ApiResponse(responseCode = "404", description = "No orders found")
    })
    public ResponseEntity<List<OrderDTO>> getOrders(
            @Parameter(description = "User ID") @RequestParam String userId) {
        return ResponseEntity.ok(orderService.getHistory(userId));
    }
}