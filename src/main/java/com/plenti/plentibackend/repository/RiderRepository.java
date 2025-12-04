package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Long> {
    Optional<Rider> findByPhoneNumber(String phoneNumber);
    List<Rider> findByStatusAndIsActiveTrue(String status);
    List<Rider> findByIsActiveTrue();
}
