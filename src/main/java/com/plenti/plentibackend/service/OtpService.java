package com.plenti.plentibackend.service;

import com.plenti.plentibackend.entity.Otp;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Service for OTP operations
 */
@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Value("${otp.expiry.minutes:10}")
    private Integer otpExpiryMinutes;

    @Value("${otp.length:6}")
    private Integer otpLength;

    @Transactional
    public String sendOtp(String phoneNumber) {
        String otpCode = generateOtp();
        
        Otp otp = new Otp();
        otp.setPhoneNumber(phoneNumber);
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        otp.setVerified(false);
        
        otpRepository.save(otp);
        
        // In a real application, send SMS here
        // smsService.sendSms(phoneNumber, "Your Plenti OTP is: " + otpCode);
        
        return otpCode; // For testing purposes only
    }

    @Transactional
    public boolean verifyOtp(String phoneNumber, String otpCode) {
        Otp otp = otpRepository.findByPhoneNumberAndOtpCodeAndVerifiedFalse(phoneNumber, otpCode)
                .orElseThrow(() -> new PlentiException("Invalid OTP"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new PlentiException("OTP has expired");
        }

        otp.setVerified(true);
        otpRepository.save(otp);
        
        return true;
    }

    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
