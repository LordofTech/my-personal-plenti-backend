package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {
    List<FlashSale> findByIsActiveTrueAndStartTimeBeforeAndEndTimeAfter(
        LocalDateTime now1, LocalDateTime now2);
    Optional<FlashSale> findByProductIdAndIsActiveTrueAndStartTimeBeforeAndEndTimeAfter(
        Long productId, LocalDateTime now1, LocalDateTime now2);
}
