package com.plenti.plentibackend.config;

import com.plenti.plentibackend.util.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
 * TESTING MODE: Using default OTP "1234" for authentication flow testing
 * Most endpoints require authentication. See securityFilterChain method for public endpoints.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
                // Authentication endpoints - Public
                .requestMatchers("/api/auth/**").permitAll()
                // Swagger documentation - Public
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // WebSocket - Public
                .requestMatchers("/ws/**").permitAll()
                // Support FAQ and help - Public
                .requestMatchers("/api/support/faq", "/api/support/help").permitAll()
                // Browsing endpoints (GET only) - Public
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/stores/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/banners").permitAll()
                // Health checks - Public
                .requestMatchers("/actuator/**").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
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
