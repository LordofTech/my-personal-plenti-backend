package com.plenti.plentibackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for OTP verification request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "OTP is required")
    private String otp;
}
