package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByReferralCode(String referralCode);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    boolean existsByEmail(String email);
}
