package com.plenti.plenti.backend.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "users") // Specifies the table name in MySQL
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate ID as Long
    private Long id; // Changed to Long for better DB handling
    private String name;
    private String email;
    private String phoneNumber; // Changed from phone to match error
    private String password; // Added for security (hash in production)
    private Date dateOfBirth;
    private String referralCode; // Added for referrals
    private double metaCoins; // Added for earnings (double to avoid lossy conversion)
    @ElementCollection
    @CollectionTable(name = "user_payment_methods", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "payment_type") // Column for keys, e.g., "card" or "bank"
    @Column(name = "payment_details") // Column for values, e.g., masked card info
    private Map<String, String> paymentMethods = new HashMap<>(); // Changed to Map for simple storage
    // Constructors
    public User() {}
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
    public double getMetaCoins() { return metaCoins; }
    public void setMetaCoins(double metaCoins) { this.metaCoins = metaCoins; }
    public Map<String, String> getPaymentMethods() { return paymentMethods; }
    public void setPaymentMethods(Map<String, String> paymentMethods) { this.paymentMethods = paymentMethods; }
}