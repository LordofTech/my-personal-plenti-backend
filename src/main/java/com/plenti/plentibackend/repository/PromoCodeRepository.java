package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for PromoCode entity
 */
@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    
    Optional<PromoCode> findByCode(String code);
}
