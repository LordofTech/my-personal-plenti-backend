package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.RiderLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiderLocationRepository extends JpaRepository<RiderLocation, Long> {
    List<RiderLocation> findByRiderIdOrderByTimestampDesc(Long riderId);
    Optional<RiderLocation> findFirstByRiderIdOrderByTimestampDesc(Long riderId);
}
