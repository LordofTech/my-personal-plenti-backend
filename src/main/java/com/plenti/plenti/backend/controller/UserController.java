package com.plenti.plenti.backend.controller;

import com.plenti.plenti.backend.dto.UserDTO;
import com.plenti.plenti.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieves user profile details in the Plenti ecommerce app, including name, email, phone, and DOB for profile screens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO> getProfile(
            @Parameter(description = "User ID") @RequestParam String userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Updates user profile details in the Plenti app for editing name, email, etc.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid update data")
    })
    public ResponseEntity<UserDTO> updateProfile(
            @Parameter(description = "User ID") @RequestParam String userId,
            @Parameter(description = "Updated user details") @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.update(userId, userDTO));
    }

    @GetMapping("/referrals")
    @Operation(summary = "Get referral information", description = "Retrieves referral code and earnings (e.g., 2000 MetaCoins) in the Plenti app, matching referral popup for sharing with friends and family.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Referral info retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Map<String, Object>> getReferrals(
            @Parameter(description = "User ID") @RequestParam String userId) {
        return ResponseEntity.ok(userService.getReferrals(userId));
    }

    @PostMapping("/refer")
    @Operation(summary = "Refer a friend", description = "Sends a referral to a friend in the Plenti app, allowing users to earn 2000 MetaCoins upon successful invite.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Referral sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email or user")
    })
    public ResponseEntity<String> referFriend(
            @Parameter(description = "User ID") @RequestParam String userId,
            @Parameter(description = "Friend's email") @RequestParam String friendEmail) {
        userService.refer(userId, friendEmail);
        return ResponseEntity.ok("Referral sent, earn 2000 MetaCoins!");
    }

    @GetMapping("/payment-methods")
    @Operation(summary = "Get saved payment methods", description = "Lists user's saved payment methods (e.g., card, bank) in the Plenti app for checkout and profile screens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment methods retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Map<String, String>> getPaymentMethods(
            @Parameter(description = "User ID") @RequestParam String userId) {
        return ResponseEntity.ok(userService.getPaymentMethods(userId));
    }

    @PostMapping("/payment-methods")
    @Operation(summary = "Add a payment method", description = "Adds a new payment method to the user's profile in the Plenti app, supporting secure trade and transparent pricing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment method added"),
            @ApiResponse(responseCode = "400", description = "Invalid method details")
    })
    public ResponseEntity<String> addPaymentMethod(
            @Parameter(description = "User ID") @RequestParam String userId,
            @Parameter(description = "Payment method details (type, details)") @RequestBody Map<String, String> paymentDetails) {
        userService.addPaymentMethod(userId, paymentDetails);
        return ResponseEntity.ok("Payment method added successfully");
    }
}