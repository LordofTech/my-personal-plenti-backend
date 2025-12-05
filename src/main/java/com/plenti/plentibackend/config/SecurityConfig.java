package com.plenti.plentibackend.config;

import com.plenti.plentibackend.util.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Security configuration for JWT authentication
 * 
 * ⚠️ TESTING MODE: All endpoints are currently public (permitAll)
 * This is a temporary configuration for testing purposes until Termii OTP API keys are obtained.
 * 
 * TO REVERT TO PRODUCTION MODE:
 * Change .anyRequest().permitAll() to .anyRequest().authenticated() in the securityFilterChain method
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF protection is disabled for stateless JWT authentication
            // JWT tokens in Authorization header provide protection against CSRF
            .csrf(csrf -> csrf.disable())
            // Enable CORS using the configuration from CorsConfig
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authorizeHttpRequests(auth -> auth
                // ⚠️ TESTING MODE: ALL endpoints are public for testing
                // Authentication endpoints
                .requestMatchers("/api/auth/**").permitAll()
                // User management
                .requestMatchers("/api/users/**").permitAll()
                // Product endpoints
                .requestMatchers("/api/products/**").permitAll()
                // Category endpoints
                .requestMatchers("/api/categories/**").permitAll()
                // Shopping cart
                .requestMatchers("/api/cart/**").permitAll()
                // Wishlist
                .requestMatchers("/api/wishlist/**").permitAll()
                // Orders
                .requestMatchers("/api/orders/**").permitAll()
                // Payments
                .requestMatchers("/api/payments/**").permitAll()
                // Addresses
                .requestMatchers("/api/addresses/**").permitAll()
                // Reviews
                .requestMatchers("/api/reviews/**").permitAll()
                // Promo codes
                .requestMatchers("/api/promo/**").permitAll()
                // Stores
                .requestMatchers("/api/stores/**").permitAll()
                // Banners
                .requestMatchers("/api/banners/**").permitAll()
                // Support & tickets
                .requestMatchers("/api/support/**").permitAll()
                // Search analytics
                .requestMatchers("/api/search/**").permitAll()
                // Riders
                .requestMatchers("/api/rider/**").permitAll()
                // Admin dashboard
                .requestMatchers("/api/admin/**").permitAll()
                // Elasticsearch
                .requestMatchers("/api/es/**").permitAll()
                // Swagger documentation
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // Health checks
                .requestMatchers("/actuator/**").permitAll()
                // WebSocket
                .requestMatchers("/ws/**").permitAll()
                // TODO: For production, change this to .authenticated() when Termii API keys are obtained
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Keep JWT filter in place for optional authentication
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
