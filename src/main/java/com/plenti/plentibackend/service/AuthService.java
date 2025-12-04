package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.AuthResponse;
import com.plenti.plentibackend.dto.LoginRequest;
import com.plenti.plentibackend.dto.OtpRequest;
import com.plenti.plentibackend.dto.RegisterRequest;
import com.plenti.plentibackend.entity.Otp;

/**
 * Service interface for authentication operations
 */
public interface AuthService {
    
    AuthResponse register(RegisterRequest request);
    
    AuthResponse verifyOtp(OtpRequest request, Otp.OtpType type);
    
    AuthResponse login(LoginRequest request);
    
    AuthResponse refreshToken(String authHeader);
    
    AuthResponse requestPasswordReset(String phoneNumber);
    
    AuthResponse resetPassword(String phoneNumber, String otp, String newPassword);
    
    AuthResponse resendRegistrationOtp(String phoneNumber);
    
    AuthResponse checkVerificationStatus(String phoneNumber);
}
