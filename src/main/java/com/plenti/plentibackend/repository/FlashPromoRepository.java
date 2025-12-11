package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.FlashPromo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlashPromoRepository extends JpaRepository<FlashPromo, Long> {
    
    List<FlashPromo> findByIsActiveTrueOrderByStartTimeDesc();
    
    Optional<FlashPromo> findByProductIdAndIsActiveTrue(Long productId);
    
    @Query("SELECT f FROM FlashPromo f WHERE f.isActive = true AND f.startTime <= :now AND f.endTime >= :now")
    List<FlashPromo> findActivePromos(LocalDateTime now);
    
    @Query("SELECT f FROM FlashPromo f WHERE f.isActive = true AND f.startTime <= :now AND f.endTime >= :now AND f.productId = :productId")
    Optional<FlashPromo> findActivePromoForProduct(Long productId, LocalDateTime now);
}
