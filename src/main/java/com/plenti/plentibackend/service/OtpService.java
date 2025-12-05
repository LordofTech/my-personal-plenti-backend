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

    @Autowired
    private SmsService smsService;

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
        otp.setType(Otp.OtpType.REGISTRATION);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        otp.setUsed(false);
        
        otpRepository.save(otp);
        
        // Send SMS via Termii
        smsService.sendOtpSms(phoneNumber, otpCode);
        
        return otpCode; // For testing purposes only
    }

    @Transactional
    public boolean verifyOtp(String phoneNumber, String otpCode) {
        Otp otp = otpRepository.findByPhoneNumberAndOtpCodeAndTypeAndUsedFalseAndExpiresAtAfter(
                phoneNumber, otpCode, Otp.OtpType.REGISTRATION, LocalDateTime.now())
                .orElseThrow(() -> new PlentiException("Invalid or expired OTP"));

        otp.setUsed(true);
        otpRepository.save(otp);
        
        return true;
    }

    private String generateOtp() {
        // TESTING MODE: Use default OTP "1234" for testing purposes
        // TODO: Revert to real OTP generation when Termii API keys are available
        // Original production code (commented out):
        // Random random = new Random();
        // StringBuilder otp = new StringBuilder();
        // for (int i = 0; i < otpLength; i++) {
        //     otp.append(random.nextInt(10));
        // }
        // return otp.toString();
        
        return "1234"; // Testing default OTP
    }
}
