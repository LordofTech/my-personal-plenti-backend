package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.PriceLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceLockRepository extends JpaRepository<PriceLock, Long> {
    List<PriceLock> findByUserIdAndIsActiveTrue(Long userId);
    Optional<PriceLock> findByUserIdAndProductIdAndIsActiveTrueAndExpiryDateAfter(
        Long userId, Long productId, LocalDateTime now);
    List<PriceLock> findByExpiryDateBeforeAndIsActiveTrue(LocalDateTime now);
}
