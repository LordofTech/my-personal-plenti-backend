package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.*;
import com.plenti.plentibackend.entity.Otp;
import com.plenti.plentibackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for authentication operations
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and OTP verification endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with phone number")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify registration OTP", description = "Verify OTP code sent during registration")
    @ApiResponse(responseCode = "200", description = "OTP verified successfully")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OtpRequest request) {
        AuthResponse response = authService.verifyOtp(request, Otp.OtpType.REGISTRATION);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-registration-otp")
    @Operation(summary = "Resend registration OTP", description = "Resend OTP for account verification")
    @ApiResponse(responseCode = "200", description = "OTP resent successfully")
    public ResponseEntity<AuthResponse> resendRegistrationOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        AuthResponse response = authService.resendRegistrationOtp(phoneNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user with phone number and password")
    @ApiResponse(responseCode = "200", description = "Login successful")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        AuthResponse response = authService.refreshToken(authHeader);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Send OTP for password reset")
    @ApiResponse(responseCode = "200", description = "Password reset OTP sent successfully")
    public ResponseEntity<AuthResponse> forgotPassword(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        AuthResponse response = authService.requestPasswordReset(phoneNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-reset-otp")
    @Operation(summary = "Verify password reset OTP", description = "Verify OTP for password reset")
    @ApiResponse(responseCode = "200", description = "Reset OTP verified successfully")
    public ResponseEntity<AuthResponse> verifyResetOtp(@Valid @RequestBody OtpRequest request) {
        AuthResponse response = authService.verifyOtp(request, Otp.OtpType.PASSWORD_RESET);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Set new password after OTP verification")
    @ApiResponse(responseCode = "200", description = "Password reset successfully")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");
        AuthResponse response = authService.resetPassword(phoneNumber, otp, newPassword);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-verification")
    @Operation(summary = "Check verification status", description = "Check if user account is verified")
    @ApiResponse(responseCode = "200", description = "Verification status retrieved")
    public ResponseEntity<AuthResponse> checkVerification(@RequestParam String phoneNumber) {
        AuthResponse response = authService.checkVerificationStatus(phoneNumber);
        return ResponseEntity.ok(response);
    }
}

