package com.plenti.plenti.backend.controller;

import com.plenti.plenti.backend.dto.CartDTO;
import com.plenti.plenti.backend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get user's cart", description = "Retrieves the cart for a specific user in the Plenti ecommerce app.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    public ResponseEntity<CartDTO> getCart(
            @Parameter(description = "User ID") @PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/add")
    @Operation(summary = "Add item to cart", description = "Adds a product to the user's cart in the Plenti app for effortless restocking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<CartDTO> addToCart(
            @Parameter(description = "User ID") @RequestParam String userId,
            @Parameter(description = "Product ID") @RequestParam String productId,
            @Parameter(description = "Quantity to add") @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.addItem(userId, productId, quantity));
    }

    @DeleteMapping("/remove/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the user's cart in the Plenti app, matching Figma cart remove options.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully, cart updated"),
            @ApiResponse(responseCode = "404", description = "Item not found in cart"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<CartDTO> removeFromCart(
            @Parameter(description = "Item ID (e.g., product ID in cart)") @PathVariable String itemId,
            @Parameter(description = "User ID") @RequestParam String userId) {
        return ResponseEntity.ok(cartService.removeItem(userId, itemId));
    }
}