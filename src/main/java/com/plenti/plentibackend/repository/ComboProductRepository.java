package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.ComboProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComboProductRepository extends JpaRepository<ComboProduct, Long> {
    List<ComboProduct> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
        LocalDateTime now1, LocalDateTime now2);
    List<ComboProduct> findByIsActiveTrue();
}
