package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for OTP entity
 */
@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    
    Optional<Otp> findByPhoneNumberAndOtpCodeAndVerifiedFalse(String phoneNumber, String otpCode);
    
    Optional<Otp> findTopByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
    
    Optional<Otp> findByPhoneNumberAndOtpCodeAndTypeAndUsedFalseAndExpiresAtAfter(
        String phoneNumber, String otpCode, Otp.OtpType type, LocalDateTime time);
    
    Optional<Otp> findByPhoneNumberAndType(String phoneNumber, Otp.OtpType type);
}
