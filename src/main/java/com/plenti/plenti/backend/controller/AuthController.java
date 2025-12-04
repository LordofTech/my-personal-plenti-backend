package com.plenti.plenti.backend.controller;

import com.plenti.plenti.backend.dto.UserDTO;
import com.plenti.plenti.backend.service.UserService;
import com.plenti.plenti.backend.util.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody UserDTO userDTO) {
        // Validate OTP if sent, etc.
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        UserDTO saved = userService.save(userDTO);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO userDTO) {
        UserDTO user = userService.findByEmail(userDTO.getEmail());
        if (passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            String token = JwtAuthenticationFilter.generateToken(user.getEmail());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.badRequest().body("Invalid credentials");
    }

    @PostMapping("/otp")
    public ResponseEntity<String> sendOtp(@RequestBody String phone) {
        // Send OTP logic (use Twilio or similar, placeholder)
        return ResponseEntity.ok("OTP sent");
    }
}