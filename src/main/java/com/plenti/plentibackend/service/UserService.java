package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.UserDTO;
import com.plenti.plentibackend.entity.User;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.UserRepository;
import com.plenti.plentibackend.util.JwtUtil;
import com.plenti.plentibackend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for user management operations
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Mapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getPhoneNumber(), user.getPassword(), new ArrayList<>());
    }

    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new PlentiException("Phone number already registered");
        }

        User user = mapper.toUserEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setReferralCode(generateReferralCode());
        user.setMetaCoins(0.0);

        User savedUser = userRepository.save(user);
        return mapper.toUserDTO(savedUser);
    }

    public Map<String, String> loginUser(String phoneNumber, String password) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PlentiException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PlentiException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getPhoneNumber());
        return Map.of("token", token, "userId", user.getId().toString());
    }

    public UserDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PlentiException("User not found"));
        return mapper.toUserDTO(user);
    }

    @Transactional
    public UserDTO updateUserProfile(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PlentiException("User not found"));

        if (userDTO.getName() != null) user.setName(userDTO.getName());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getDateOfBirth() != null) user.setDateOfBirth(userDTO.getDateOfBirth());

        User updatedUser = userRepository.save(user);
        return mapper.toUserDTO(updatedUser);
    }

    @Transactional
    public void addPaymentMethod(Long userId, String type, String details) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PlentiException("User not found"));
        user.getPaymentMethods().put(type, details);
        userRepository.save(user);
    }

    @Transactional
    public void rewardReferral(String referralCode) {
        User user = userRepository.findByReferralCode(referralCode)
                .orElseThrow(() -> new PlentiException("Invalid referral code"));
        user.setMetaCoins(user.getMetaCoins() + 2000);
        userRepository.save(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toUserDTO)
                .toList();
    }

    private String generateReferralCode() {
        return "PLT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
