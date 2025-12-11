package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.PointsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointsTransactionRepository extends JpaRepository<PointsTransaction, Long> {
    List<PointsTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<PointsTransaction> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);
}
