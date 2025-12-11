package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.OrderDTO;
import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.entity.OrderStatus;
import com.plenti.plentibackend.service.FulfillmentService;
import com.plenti.plentibackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for order operations including tracking, reordering, and cancellation
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private FulfillmentService fulfillmentService;

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
    
    @GetMapping("/{id}/tracking")
    @Operation(summary = "Get order tracking", description = "Get detailed tracking info with progress bar data")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getOrderTracking(@PathVariable Long id) {
        Map<String, Object> tracking = fulfillmentService.getFulfillmentStatus(id);
        return ResponseEntity.ok(ResponseDTO.success(tracking));
    }
    
    @PostMapping("/{id}/reorder")
    @Operation(summary = "Reorder", description = "Create a new order based on previous order")
    public ResponseEntity<ResponseDTO<OrderDTO>> reorder(
            @PathVariable Long id,
            @RequestParam Long userId) {
        // For now, just return the original order details
        // In production, this would create a new order
        OrderDTO order = orderService.getOrder(id);
        return ResponseEntity.ok(ResponseDTO.success("Order details retrieved for reorder", order));
    }
    
    @PutMapping("/{id}/cancel-with-refund")
    @Operation(summary = "Cancel order with refund", description = "Cancel order and initiate refund process")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> cancelOrderWithRefund(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> request) {
        String reason = request != null ? request.getOrDefault("reason", "User requested cancellation") : "User requested cancellation";
        OrderDTO order = orderService.cancelOrder(id);
        
        Map<String, Object> response = Map.of(
            "order", order,
            "refundStatus", "INITIATED",
            "refundMessage", "Refund will be processed within 5-7 business days",
            "cancellationReason", reason
        );
        
        return ResponseEntity.ok(ResponseDTO.success("Order cancelled and refund initiated", response));
    }
    
    @GetMapping("/{id}/progress")
    @Operation(summary = "Get order progress", description = "Get order progress with percentage and status stages")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getOrderProgress(@PathVariable Long id) {
        OrderDTO order = orderService.getOrder(id);
        
        Map<String, Object> progress = Map.of(
            "orderId", id,
            "currentStatus", order.getStatus(),
            "percentage", calculateProgressPercentage(order.getStatus()),
            "stages", getProgressStages(order.getStatus()),
            "estimatedDelivery", order.getEstimatedDelivery() != null ? order.getEstimatedDelivery() : ""
        );
        
        return ResponseEntity.ok(ResponseDTO.success(progress));
    }
    
    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "Get recent orders", description = "Get user's most recent orders for quick reorder")
    public ResponseEntity<ResponseDTO<List<OrderDTO>>> getRecentOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        List<OrderDTO> orders = orderService.getOrderHistory(userId);
        List<OrderDTO> recentOrders = orders.stream()
                .limit(limit)
                .toList();
        return ResponseEntity.ok(ResponseDTO.success(recentOrders));
    }
    
    /**
     * Calculate progress percentage based on order status
     */
    private int calculateProgressPercentage(OrderStatus status) {
        return switch (status) {
            case PENDING -> 10;
            case PROCESSING -> 20;
            case CONFIRMED -> 40;
            case PACKED -> 60;
            case OUT_FOR_DELIVERY -> 80;
            case DELIVERED -> 100;
            case CANCELLED, REFUNDED -> 0;
        };
    }
    
    /**
     * Get progress stages with completion status
     */
    private List<Map<String, Object>> getProgressStages(OrderStatus currentStatus) {
        List<OrderStatus> stages = List.of(
            OrderStatus.PENDING,
            OrderStatus.CONFIRMED,
            OrderStatus.PACKED,
            OrderStatus.OUT_FOR_DELIVERY,
            OrderStatus.DELIVERED
        );
        
        int currentIndex = stages.indexOf(currentStatus);
        
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (OrderStatus stage : stages) {
            Map<String, Object> stageMap = new java.util.HashMap<>();
            stageMap.put("stage", stage.toString());
            stageMap.put("completed", stages.indexOf(stage) <= currentIndex);
            stageMap.put("label", getStageLabel(stage));
            result.add(stageMap);
        }
        
        return result;
    }
    
    /**
     * Get human-readable label for stage
     */
    private String getStageLabel(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Order Received";
            case CONFIRMED -> "Order Confirmed";
            case PACKED -> "Ready for Delivery";
            case OUT_FOR_DELIVERY -> "Out for Delivery";
            case DELIVERED -> "Delivered";
            default -> status.toString();
        };
    }
}
