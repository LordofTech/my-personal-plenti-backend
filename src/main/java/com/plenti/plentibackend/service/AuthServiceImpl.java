package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.AuthResponse;
import com.plenti.plentibackend.dto.LoginRequest;
import com.plenti.plentibackend.dto.OtpRequest;
import com.plenti.plentibackend.dto.RegisterRequest;
import com.plenti.plentibackend.entity.Otp;
import com.plenti.plentibackend.entity.Role;
import com.plenti.plentibackend.entity.User;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.OtpRepository;
import com.plenti.plentibackend.repository.RoleRepository;
import com.plenti.plentibackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of AuthService for authentication operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SmsService smsService;

    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int OTP_LENGTH = 6;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new PlentiException("Phone number already registered");
        }

        // Check if email already exists (if provided)
        if (request.getEmail() != null && !request.getEmail().isEmpty() 
                && userRepository.existsByEmail(request.getEmail())) {
            throw new PlentiException("Email already registered");
        }

        // Create user with enabled = false
        User user = new User();
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setEnabled(false);
        user.setReferralCode(generateReferralCode());
        user.setMetaCoins(0.0);

        // Assign default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new PlentiException("Default USER role not found"));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        // Generate and send OTP
        String otpCode = generateOtp();
        saveOtp(request.getPhoneNumber(), otpCode, Otp.OtpType.REGISTRATION);
        smsService.sendOtpSms(request.getPhoneNumber(), otpCode);

        return AuthResponse.builder()
                .success(true)
                .message("Registration successful. Please verify OTP sent to your phone.")
                .phoneNumber(request.getPhoneNumber())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(OtpRequest request, Otp.OtpType type) {
        // Find OTP
        Otp otp = otpRepository.findByPhoneNumberAndOtpCodeAndTypeAndUsedFalseAndExpiresAtAfter(
                request.getPhoneNumber(), request.getOtp(), type, LocalDateTime.now())
                .orElseThrow(() -> new PlentiException("Invalid or expired OTP"));

        // Mark OTP as used
        otp.setUsed(true);
        otpRepository.save(otp);

        // Find user
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new PlentiException("User not found"));

        if (type == Otp.OtpType.REGISTRATION) {
            // Enable user account
            user.setEnabled(true);
            userRepository.save(user);

            // Generate tokens
            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return AuthResponse.builder()
                    .success(true)
                    .message("OTP verified successfully")
                    .token(token)
                    .refreshToken(refreshToken)
                    .phoneNumber(user.getPhoneNumber())
                    .name(user.getName())
                    .userId(user.getId())
                    .isVerified(true)
                    .build();
        } else if (type == Otp.OtpType.PASSWORD_RESET) {
            return AuthResponse.builder()
                    .success(true)
                    .message("OTP verified. You can now reset your password.")
                    .phoneNumber(user.getPhoneNumber())
                    .build();
        }

        throw new PlentiException("Invalid OTP type");
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Find user
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new PlentiException("Invalid credentials"));

        // Check if account is locked
        if (user.getAccountLocked()) {
            if (user.getLockTime() != null && 
                user.getLockTime().plusMinutes(LOCK_DURATION_MINUTES).isAfter(LocalDateTime.now())) {
                long minutesLeft = java.time.Duration.between(LocalDateTime.now(), 
                        user.getLockTime().plusMinutes(LOCK_DURATION_MINUTES)).toMinutes();
                throw new PlentiException("Account is locked. Please try again in " + minutesLeft + " minutes.");
            } else {
                // Auto-unlock if lock time expired
                user.setAccountLocked(false);
                user.setFailedLoginAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
            }
        }

        // Check if account is enabled
        if (!user.getEnabled()) {
            throw new PlentiException("Account not verified. Please verify your OTP first.");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Increment failed attempts
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            if (user.getFailedLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                // Lock account
                user.setAccountLocked(true);
                user.setLockTime(LocalDateTime.now());
                userRepository.save(user);
                throw new PlentiException("Account locked due to too many failed login attempts. Please try again in " 
                        + LOCK_DURATION_MINUTES + " minutes.");
            }

            userRepository.save(user);
            throw new PlentiException("Invalid credentials. " + 
                    (MAX_LOGIN_ATTEMPTS - user.getFailedLoginAttempts()) + " attempts remaining.");
        }

        // Successful login - reset failed attempts
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);

        // Generate tokens
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .success(true)
                .message("Login successful")
                .token(token)
                .refreshToken(refreshToken)
                .phoneNumber(user.getPhoneNumber())
                .name(user.getName())
                .userId(user.getId())
                .build();
    }

    @Override
    public AuthResponse refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new PlentiException("Invalid authorization header");
        }

        String refreshToken = authHeader.substring(7);
        String phoneNumber = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PlentiException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new PlentiException("Invalid refresh token");
        }

        // Generate new access token
        String newToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .success(true)
                .message("Token refreshed successfully")
                .token(newToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse requestPasswordReset(String phoneNumber) {
        // Check if user exists
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PlentiException("User not found"));

        // Generate and send OTP
        String otpCode = generateOtp();
        saveOtp(phoneNumber, otpCode, Otp.OtpType.PASSWORD_RESET);
        smsService.sendOtpSms(phoneNumber, otpCode);

        return AuthResponse.builder()
                .success(true)
                .message("Password reset OTP sent to your phone")
                .phoneNumber(phoneNumber)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse resetPassword(String phoneNumber, String otp, String newPassword) {
        // Verify OTP first
        Otp otpEntity = otpRepository.findByPhoneNumberAndOtpCodeAndTypeAndUsedFalseAndExpiresAtAfter(
                phoneNumber, otp, Otp.OtpType.PASSWORD_RESET, LocalDateTime.now())
                .orElseThrow(() -> new PlentiException("Invalid or expired OTP"));

        // Mark OTP as used
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);

        // Update password
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PlentiException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        userRepository.save(user);

        return AuthResponse.builder()
                .success(true)
                .message("Password reset successfully")
                .phoneNumber(phoneNumber)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse resendRegistrationOtp(String phoneNumber) {
        // Check if user exists
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PlentiException("User not found"));

        if (user.getEnabled()) {
            throw new PlentiException("Account already verified");
        }

        // Generate and send new OTP
        String otpCode = generateOtp();
        saveOtp(phoneNumber, otpCode, Otp.OtpType.REGISTRATION);
        smsService.sendOtpSms(phoneNumber, otpCode);

        return AuthResponse.builder()
                .success(true)
                .message("OTP resent successfully")
                .phoneNumber(phoneNumber)
                .build();
    }

    @Override
    public AuthResponse checkVerificationStatus(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PlentiException("User not found"));

        return AuthResponse.builder()
                .success(true)
                .message("Verification status retrieved")
                .phoneNumber(phoneNumber)
                .isVerified(user.getEnabled())
                .build();
    }

    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private void saveOtp(String phoneNumber, String otpCode, Otp.OtpType type) {
        // Delete existing OTP for this phone and type if any
        otpRepository.findByPhoneNumberAndType(phoneNumber, type)
                .ifPresent(otpRepository::delete);

        Otp otp = new Otp();
        otp.setPhoneNumber(phoneNumber);
        otp.setOtpCode(otpCode);
        otp.setType(type);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otp.setUsed(false);

        otpRepository.save(otp);
    }

    private String generateReferralCode() {
        return "PLT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
