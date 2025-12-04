package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.dto.UserDTO;
import com.plenti.plentibackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for user management operations
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get authenticated user's profile")
    public ResponseEntity<ResponseDTO<UserDTO>> getProfile(@RequestParam Long userId) {
        UserDTO user = userService.getUserProfile(userId);
        return ResponseEntity.ok(ResponseDTO.success(user));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update user profile information")
    public ResponseEntity<ResponseDTO<UserDTO>> updateProfile(
            @RequestParam Long userId,
            @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUserProfile(userId, userDTO);
        return ResponseEntity.ok(ResponseDTO.success("Profile updated successfully", updatedUser));
    }

    @PostMapping("/refer")
    @Operation(summary = "Use referral code", description = "Apply referral code to reward referrer")
    public ResponseEntity<ResponseDTO<String>> useReferral(@RequestBody Map<String, String> request) {
        String referralCode = request.get("referralCode");
        userService.rewardReferral(referralCode);
        return ResponseEntity.ok(ResponseDTO.success("Referral applied successfully", referralCode));
    }

    @GetMapping("/payment-methods")
    @Operation(summary = "Get payment methods", description = "Get user's saved payment methods")
    public ResponseEntity<ResponseDTO<Map<String, String>>> getPaymentMethods(@RequestParam Long userId) {
        UserDTO user = userService.getUserProfile(userId);
        return ResponseEntity.ok(ResponseDTO.success(user.getPaymentMethods()));
    }

    @PostMapping("/payment-methods")
    @Operation(summary = "Add payment method", description = "Add a new payment method")
    public ResponseEntity<ResponseDTO<String>> addPaymentMethod(
            @RequestParam Long userId,
            @RequestBody Map<String, String> paymentMethod) {
        String type = paymentMethod.get("type");
        String details = paymentMethod.get("details");
        userService.addPaymentMethod(userId, type, details);
        return ResponseEntity.ok(ResponseDTO.success("Payment method added successfully", type));
    }
}
