package com.plenti.plenti.backend.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;  // Match entity
    private String password;  // For signup/login (don't expose in responses)
    private LocalDate dateOfBirth;  // Use LocalDate for modern handling
    private String referralCode;
    private double metaCoins;  // Double to match entity
    private List<Map<String, String>> paymentMethods;

    // Constructors
    public UserDTO() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
    public double getMetaCoins() { return metaCoins; }
    public void setMetaCoins(double metaCoins) { this.metaCoins = metaCoins; }
    public List<Map<String, String>> getPaymentMethods() { return paymentMethods; }
    public void setPaymentMethods(List<Map<String, String>> paymentMethods) { this.paymentMethods = paymentMethods; }
}