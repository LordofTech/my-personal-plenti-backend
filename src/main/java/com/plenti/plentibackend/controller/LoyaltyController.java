package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.PlentiPointsDTO;
import com.plenti.plentibackend.dto.PointsTransactionDTO;
import com.plenti.plentibackend.dto.PriceLockDTO;
import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.service.LoyaltyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for loyalty points and price lock operations
 */
@RestController
@RequestMapping("/api/loyalty")
@Tag(name = "Loyalty", description = "Loyalty points and price lock endpoints")
public class LoyaltyController {

    @Autowired
    private LoyaltyService loyaltyService;

    @GetMapping("/balance")
    @Operation(summary = "Get points balance", description = "Get user's loyalty points balance and tier")
    public ResponseEntity<ResponseDTO<PlentiPointsDTO>> getBalance(@RequestParam Long userId) {
        PlentiPointsDTO balance = loyaltyService.getPointsBalance(userId);
        return ResponseEntity.ok(ResponseDTO.success(balance));
    }

    @GetMapping("/history")
    @Operation(summary = "Get points history", description = "Get user's points transaction history")
    public ResponseEntity<ResponseDTO<List<PointsTransactionDTO>>> getHistory(@RequestParam Long userId) {
        List<PointsTransactionDTO> history = loyaltyService.getPointsHistory(userId);
        return ResponseEntity.ok(ResponseDTO.success(history));
    }

    @PostMapping("/redeem")
    @Operation(summary = "Redeem points", description = "Redeem loyalty points for an order")
    public ResponseEntity<ResponseDTO<PlentiPointsDTO>> redeemPoints(@RequestBody java.util.Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Integer points = Integer.valueOf(request.get("points").toString());
        Long orderId = request.get("orderId") != null ? Long.valueOf(request.get("orderId").toString()) : null;
        
        PlentiPointsDTO result = loyaltyService.redeemPoints(userId, points, orderId);
        return ResponseEntity.ok(ResponseDTO.success("Points redeemed successfully", result));
    }

    @PostMapping("/pricelock/{productId}")
    @Operation(summary = "Apply price lock", description = "Lock current price of a product for 30 days")
    public ResponseEntity<ResponseDTO<PriceLockDTO>> applyPriceLock(
            @PathVariable Long productId,
            @RequestParam Long userId) {
        PriceLockDTO priceLock = loyaltyService.applyPricelock(userId, productId);
        return ResponseEntity.ok(ResponseDTO.success("Price lock applied successfully", priceLock));
    }

    @GetMapping("/pricelocks")
    @Operation(summary = "Get active price locks", description = "Get all active price locks for user")
    public ResponseEntity<ResponseDTO<List<PriceLockDTO>>> getPriceLocks(@RequestParam Long userId) {
        List<PriceLockDTO> priceLocks = loyaltyService.getActivePricelocks(userId);
        return ResponseEntity.ok(ResponseDTO.success(priceLocks));
    }
}
