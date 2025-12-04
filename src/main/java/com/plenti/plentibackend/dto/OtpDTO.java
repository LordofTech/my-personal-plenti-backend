package com.plenti.plentibackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for OTP operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpDTO {
    
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    
    private String otpCode;
    private String message;
}
