package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.dto.WishlistDTO;
import com.plenti.plentibackend.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for wishlist operations
 */
@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist", description = "Wishlist management endpoints")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get wishlist", description = "Get user's wishlist")
    public ResponseEntity<ResponseDTO<WishlistDTO>> getWishlist(@PathVariable Long userId) {
        WishlistDTO wishlist = wishlistService.getWishlist(userId);
        return ResponseEntity.ok(ResponseDTO.success(wishlist));
    }

    @PostMapping("/add")
    @Operation(summary = "Add to wishlist", description = "Add product to wishlist")
    public ResponseEntity<ResponseDTO<WishlistDTO>> addToWishlist(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long productId = request.get("productId");
        WishlistDTO wishlist = wishlistService.addToWishlist(userId, productId);
        return ResponseEntity.ok(ResponseDTO.success("Product added to wishlist", wishlist));
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Remove from wishlist", description = "Remove product from wishlist")
    public ResponseEntity<ResponseDTO<WishlistDTO>> removeFromWishlist(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long productId = request.get("productId");
        WishlistDTO wishlist = wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok(ResponseDTO.success("Product removed from wishlist", wishlist));
    }

    @PostMapping("/move-to-cart")
    @Operation(summary = "Move to cart", description = "Move wishlist item to cart")
    public ResponseEntity<ResponseDTO<String>> moveToCart(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long productId = request.get("productId");
        wishlistService.moveToCart(userId, productId);
        return ResponseEntity.ok(ResponseDTO.success("Product moved to cart", "moved"));
    }
}
