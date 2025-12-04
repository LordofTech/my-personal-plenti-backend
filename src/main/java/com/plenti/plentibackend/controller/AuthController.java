package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.OtpDTO;
import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.dto.UserDTO;
import com.plenti.plentibackend.service.OtpService;
import com.plenti.plentibackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for authentication operations
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and OTP verification endpoints")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Create a new user account")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    public ResponseEntity<ResponseDTO<UserDTO>> signup(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.registerUser(userDTO);
        return ResponseEntity.ok(ResponseDTO.success("User registered successfully", createdUser));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and get JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    public ResponseEntity<ResponseDTO<Map<String, String>>> login(@RequestBody Map<String, String> credentials) {
        String phoneNumber = credentials.get("phoneNumber");
        String password = credentials.get("password");
        Map<String, String> tokenMap = userService.loginUser(phoneNumber, password);
        return ResponseEntity.ok(ResponseDTO.success("Login successful", tokenMap));
    }

    @PostMapping("/otp/send")
    @Operation(summary = "Send OTP", description = "Send OTP verification code to phone number")
    @ApiResponse(responseCode = "200", description = "OTP sent successfully")
    public ResponseEntity<ResponseDTO<OtpDTO>> sendOtp(@Valid @RequestBody OtpDTO otpDTO) {
        otpService.sendOtp(otpDTO.getPhoneNumber());
        OtpDTO response = new OtpDTO();
        response.setPhoneNumber(otpDTO.getPhoneNumber());
        response.setMessage("OTP sent successfully");
        // Note: OTP is sent via SMS, not returned in response for security
        return ResponseEntity.ok(ResponseDTO.success(response));
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Verify OTP", description = "Verify OTP code for phone number")
    @ApiResponse(responseCode = "200", description = "OTP verified successfully")
    public ResponseEntity<ResponseDTO<String>> verifyOtp(@Valid @RequestBody OtpDTO otpDTO) {
        boolean verified = otpService.verifyOtp(otpDTO.getPhoneNumber(), otpDTO.getOtpCode());
        return ResponseEntity.ok(ResponseDTO.success("OTP verified successfully", "verified"));
    }
}
