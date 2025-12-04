package com.plenti.plenti.backend.config;

import com.plenti.plenti.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.plenti.plenti.backend.util.JwtAuthenticationFilter;
import org.springframework.security.core.userdetails.User;
import java.util.ArrayList;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                //.anyRequest().authenticated() // Comment out this line (or replace with below to allow all for testing)

                .anyRequest().permitAll()  // Add this instead to skip auth checks on all other endpoints
            )
            //.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // Comment out this entire line to remove JWT filter
        ;
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(userDetailsService());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.plenti.plenti.backend.dto.UserDTO userDTO = userService.findByEmail(username);
            if (userDTO == null) {
                throw new UsernameNotFoundException("User not found with email: " + username);
            }
            return new User(userDTO.getEmail(), userDTO.getPassword(), Collections.emptyList());
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}