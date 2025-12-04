package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.CartDTO;
import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for cart operations
 */
@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Shopping cart endpoints")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get cart", description = "Get user's shopping cart")
    public ResponseEntity<ResponseDTO<CartDTO>> getCart(@PathVariable Long userId) {
        CartDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(ResponseDTO.success(cart));
    }

    @PostMapping("/add")
    @Operation(summary = "Add to cart", description = "Add item to shopping cart")
    public ResponseEntity<ResponseDTO<CartDTO>> addToCart(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long productId = Long.valueOf(request.get("productId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        CartDTO cart = cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok(ResponseDTO.success("Item added to cart", cart));
    }

    @DeleteMapping("/remove/{userId}/{productId}")
    @Operation(summary = "Remove from cart", description = "Remove item from shopping cart")
    public ResponseEntity<ResponseDTO<CartDTO>> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        CartDTO cart = cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(ResponseDTO.success("Item removed from cart", cart));
    }

    @PutMapping("/update")
    @Operation(summary = "Update quantity", description = "Update item quantity in cart")
    public ResponseEntity<ResponseDTO<CartDTO>> updateQuantity(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long productId = Long.valueOf(request.get("productId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        CartDTO cart = cartService.updateQuantity(userId, productId, quantity);
        return ResponseEntity.ok(ResponseDTO.success("Quantity updated", cart));
    }

    @DeleteMapping("/clear/{userId}")
    @Operation(summary = "Clear cart", description = "Clear all items from cart")
    public ResponseEntity<ResponseDTO<String>> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ResponseDTO.success("Cart cleared successfully", "cleared"));
    }
}
