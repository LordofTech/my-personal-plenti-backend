package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.*;
import com.plenti.plentibackend.entity.Otp;
import com.plenti.plentibackend.entity.Role;
import com.plenti.plentibackend.entity.User;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.OtpRepository;
import com.plenti.plentibackend.repository.RoleRepository;
import com.plenti.plentibackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private OtpRequest otpRequest;
    private User user;
    private Role userRole;
    private Otp otp;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setPhoneNumber("+2348012345678");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");

        loginRequest = new LoginRequest();
        loginRequest.setPhoneNumber("+2348012345678");
        loginRequest.setPassword("password123");

        otpRequest = new OtpRequest();
        otpRequest.setPhoneNumber("+2348012345678");
        otpRequest.setOtp("123456");

        userRole = new Role();
        userRole.setName("USER");
        userRole.setDescription("Regular user");
        userRole.setPermissions(new HashSet<>());

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setPhoneNumber("+2348012345678");
        user.setPassword("encodedPassword");
        user.setEmail("test@example.com");
        user.setEnabled(false);
        user.setRoles(Set.of(userRole));
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);

        otp = new Otp();
        otp.setId(1L);
        otp.setPhoneNumber("+2348012345678");
        otp.setOtpCode("123456");
        otp.setType(Otp.OtpType.REGISTRATION);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otp.setUsed(false);
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(otpRepository.save(any(Otp.class))).thenReturn(otp);
        when(smsService.sendOtpSms(anyString(), anyString())).thenReturn(true);

        AuthResponse response = authService.register(registerRequest);

        assertTrue(response.isSuccess());
        assertEquals("Registration successful. Please verify OTP sent to your phone.", response.getMessage());
        assertEquals("+2348012345678", response.getPhoneNumber());
        verify(userRepository).save(any(User.class));
        verify(otpRepository).save(any(Otp.class));
        verify(smsService).sendOtpSms(anyString(), anyString());
    }

    @Test
    void testRegister_PhoneNumberAlreadyExists() {
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(true);

        PlentiException exception = assertThrows(PlentiException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Phone number already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testVerifyOtp_RegistrationSuccess() {
        user.setEnabled(false);
        when(otpRepository.findByPhoneNumberAndOtpCodeAndTypeAndUsedFalseAndExpiresAtAfter(
                anyString(), anyString(), any(Otp.OtpType.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(otp));
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("token123");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken123");

        AuthResponse response = authService.verifyOtp(otpRequest, Otp.OtpType.REGISTRATION);

        assertTrue(response.isSuccess());
        assertEquals("OTP verified successfully", response.getMessage());
        assertEquals("token123", response.getToken());
        assertEquals("refreshToken123", response.getRefreshToken());
        assertTrue(response.getIsVerified());
        verify(otpRepository).save(argThat(o -> o.getUsed()));
        verify(userRepository).save(argThat(u -> u.getEnabled()));
    }

    @Test
    void testVerifyOtp_InvalidOtp() {
        when(otpRepository.findByPhoneNumberAndOtpCodeAndTypeAndUsedFalseAndExpiresAtAfter(
                anyString(), anyString(), any(Otp.OtpType.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        PlentiException exception = assertThrows(PlentiException.class, () -> {
            authService.verifyOtp(otpRequest, Otp.OtpType.REGISTRATION);
        });

        assertEquals("Invalid or expired OTP", exception.getMessage());
    }

    @Test
    void testLogin_Success() {
        user.setEnabled(true);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("token123");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken123");

        AuthResponse response = authService.login(loginRequest);

        assertTrue(response.isSuccess());
        assertEquals("Login successful", response.getMessage());
        assertEquals("token123", response.getToken());
        assertEquals("refreshToken123", response.getRefreshToken());
        verify(userRepository).save(argThat(u -> u.getFailedLoginAttempts() == 0));
    }

    @Test
    void testLogin_InvalidCredentials() {
        user.setEnabled(true);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        PlentiException exception = assertThrows(PlentiException.class, () -> {
            authService.login(loginRequest);
        });

        assertTrue(exception.getMessage().contains("Invalid credentials"));
        verify(userRepository).save(argThat(u -> u.getFailedLoginAttempts() == 1));
    }

    @Test
    void testLogin_AccountLocked() {
        user.setEnabled(true);
        user.setAccountLocked(true);
        user.setLockTime(LocalDateTime.now());
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        PlentiException exception = assertThrows(PlentiException.class, () -> {
            authService.login(loginRequest);
        });

        assertTrue(exception.getMessage().contains("Account is locked"));
    }

    @Test
    void testLogin_AccountNotVerified() {
        user.setEnabled(false);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        PlentiException exception = assertThrows(PlentiException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Account not verified. Please verify your OTP first.", exception.getMessage());
    }

    @Test
    void testLogin_AccountLockedAfterMaxAttempts() {
        user.setEnabled(true);
        user.setFailedLoginAttempts(4);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        PlentiException exception = assertThrows(PlentiException.class, () -> {
            authService.login(loginRequest);
        });

        assertTrue(exception.getMessage().contains("Account locked"));
        verify(userRepository).save(argThat(u -> u.getAccountLocked() && u.getFailedLoginAttempts() == 5));
    }

    @Test
    void testRefreshToken_Success() {
        user.setEnabled(true);
        String authHeader = "Bearer refreshToken123";
        when(jwtService.extractUsername(anyString())).thenReturn("+2348012345678");
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(anyString(), any(User.class))).thenReturn(true);
        when(jwtService.generateToken(any(User.class))).thenReturn("newToken123");

        AuthResponse response = authService.refreshToken(authHeader);

        assertTrue(response.isSuccess());
        assertEquals("Token refreshed successfully", response.getMessage());
        assertEquals("newToken123", response.getToken());
        assertEquals("refreshToken123", response.getRefreshToken());
    }

    @Test
    void testRefreshToken_InvalidHeader() {
        PlentiException exception = assertThrows(PlentiException.class, () -> {
            authService.refreshToken("InvalidHeader");
        });

        assertEquals("Invalid authorization header", exception.getMessage());
    }

    @Test
    void testRequestPasswordReset_Success() {
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(otpRepository.findByPhoneNumberAndType(anyString(), any(Otp.OtpType.class)))
                .thenReturn(Optional.empty());
        when(otpRepository.save(any(Otp.class))).thenReturn(otp);
        when(smsService.sendOtpSms(anyString(), anyString())).thenReturn(true);

        AuthResponse response = authService.requestPasswordReset("+2348012345678");

        assertTrue(response.isSuccess());
        assertEquals("Password reset OTP sent to your phone", response.getMessage());
        verify(otpRepository).save(any(Otp.class));
        verify(smsService).sendOtpSms(anyString(), anyString());
    }

    @Test
    void testResetPassword_Success() {
        otp.setType(Otp.OtpType.PASSWORD_RESET);
        when(otpRepository.findByPhoneNumberAndOtpCodeAndTypeAndUsedFalseAndExpiresAtAfter(
                anyString(), anyString(), any(Otp.OtpType.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(otp));
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        AuthResponse response = authService.resetPassword("+2348012345678", "123456", "newPassword123");

        assertTrue(response.isSuccess());
        assertEquals("Password reset successfully", response.getMessage());
        verify(otpRepository).save(argThat(o -> o.getUsed()));
        verify(userRepository).save(argThat(u -> u.getFailedLoginAttempts() == 0 && !u.getAccountLocked()));
    }

    @Test
    void testResendRegistrationOtp_Success() {
        user.setEnabled(false);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(otpRepository.findByPhoneNumberAndType(anyString(), any(Otp.OtpType.class)))
                .thenReturn(Optional.empty());
        when(otpRepository.save(any(Otp.class))).thenReturn(otp);
        when(smsService.sendOtpSms(anyString(), anyString())).thenReturn(true);

        AuthResponse response = authService.resendRegistrationOtp("+2348012345678");

        assertTrue(response.isSuccess());
        assertEquals("OTP resent successfully", response.getMessage());
        verify(otpRepository).save(any(Otp.class));
        verify(smsService).sendOtpSms(anyString(), anyString());
    }

    @Test
    void testResendRegistrationOtp_AlreadyVerified() {
        user.setEnabled(true);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        PlentiException exception = assertThrows(PlentiException.class, () -> {
            authService.resendRegistrationOtp("+2348012345678");
        });

        assertEquals("Account already verified", exception.getMessage());
    }

    @Test
    void testCheckVerificationStatus_Verified() {
        user.setEnabled(true);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        AuthResponse response = authService.checkVerificationStatus("+2348012345678");

        assertTrue(response.isSuccess());
        assertTrue(response.getIsVerified());
    }

    @Test
    void testCheckVerificationStatus_NotVerified() {
        user.setEnabled(false);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        AuthResponse response = authService.checkVerificationStatus("+2348012345678");

        assertTrue(response.isSuccess());
        assertFalse(response.getIsVerified());
    }
}
