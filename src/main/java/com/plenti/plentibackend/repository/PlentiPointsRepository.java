package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.PlentiPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlentiPointsRepository extends JpaRepository<PlentiPoints, Long> {
    Optional<PlentiPoints> findByUserId(Long userId);
}
