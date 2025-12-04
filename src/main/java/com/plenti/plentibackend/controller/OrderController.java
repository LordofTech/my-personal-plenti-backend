package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.OrderDTO;
import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.entity.OrderStatus;
import com.plenti.plentibackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for order operations
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "Place order", description = "Create a new order from cart")
    public ResponseEntity<ResponseDTO<OrderDTO>> placeOrder(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String deliveryAddress = request.get("deliveryAddress").toString();
        String promoCode = request.getOrDefault("promoCode", "").toString();
        Double discountAmount = request.containsKey("discountAmount") ? 
                Double.valueOf(request.get("discountAmount").toString()) : 0.0;
        
        OrderDTO order = orderService.placeOrder(userId, deliveryAddress, promoCode, discountAmount);
        return ResponseEntity.ok(ResponseDTO.success("Order placed successfully", order));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Track order", description = "Get order details and tracking info")
    public ResponseEntity<ResponseDTO<OrderDTO>> getOrder(@PathVariable Long id) {
        OrderDTO order = orderService.getOrder(id);
        return ResponseEntity.ok(ResponseDTO.success(order));
    }

    @GetMapping
    @Operation(summary = "Get order history", description = "Get user's order history")
    public ResponseEntity<ResponseDTO<List<OrderDTO>>> getOrderHistory(@RequestParam Long userId) {
        List<OrderDTO> orders = orderService.getOrderHistory(userId);
        return ResponseEntity.ok(ResponseDTO.success(orders));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Update order status (admin only)")
    public ResponseEntity<ResponseDTO<OrderDTO>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        OrderDTO order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ResponseDTO.success("Order status updated", order));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order")
    public ResponseEntity<ResponseDTO<OrderDTO>> cancelOrder(@PathVariable Long id) {
        OrderDTO order = orderService.cancelOrder(id);
        return ResponseEntity.ok(ResponseDTO.success("Order cancelled successfully", order));
    }

    @PutMapping("/{id}/assign-rider")
    @Operation(summary = "Assign rider", description = "Assign delivery rider to order (admin only)")
    public ResponseEntity<ResponseDTO<OrderDTO>> assignRider(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String riderId = request.get("riderId");
        String riderName = request.get("riderName");
        OrderDTO order = orderService.assignRider(id, riderId, riderName);
        return ResponseEntity.ok(ResponseDTO.success("Rider assigned successfully", order));
    }
}
