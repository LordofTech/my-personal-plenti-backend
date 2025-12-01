package com.plenti.plenti.backend.service;

import com.plenti.plenti.backend.dto.UserDTO;
import com.plenti.plenti.backend.entity.User;
import com.plenti.plenti.backend.repository.jpa.UserRepository; 
import com.plenti.plenti.backend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserDTO save(UserDTO userDTO) {
        User user = Mapper.toUser(userDTO);
        return Mapper.toUserDTO(userRepository.save(user));
    }

    public UserDTO findById(String id) {
        return userRepository.findById(Long.parseLong(id)).map(Mapper::toUserDTO).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return Mapper.toUserDTO(user);
    }

    public UserDTO update(String id, UserDTO userDTO) {
        User user = userRepository.findById(Long.parseLong(id)).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        // Convert LocalDate to Date if present
        if (userDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(Date.from(userDTO.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        return Mapper.toUserDTO(userRepository.save(user));
    }

    public Map<String, Object> getReferrals(String userId) {
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, Object> referrals = new HashMap<>();
        referrals.put("code", user.getReferralCode() != null ? user.getReferralCode() : "No code yet"); // Assume User has referralCode field
        referrals.put("earnings", user.getMetaCoins()); // Earnings as MetaCoins
        return referrals;
    }

    public void refer(String userId, String friendEmail) {
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        user.setMetaCoins(user.getMetaCoins() + 2000);
        userRepository.save(user);
        // Send email logic (placeholder; integrate real email service like SendGrid for production)
        System.out.println("Referral email sent to " + friendEmail);
    }

    public Map<String, String> getPaymentMethods(String userId) {
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPaymentMethods(); // Now assumes field exists in User entity
    }

    public void addPaymentMethod(String userId, Map<String, String> paymentDetails) {
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        user.getPaymentMethods().putAll(paymentDetails); // Add multiple entries from the map
        userRepository.save(user);
    }
}