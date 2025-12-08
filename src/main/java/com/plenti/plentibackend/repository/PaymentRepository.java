package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Payment entity
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReference(String reference);

    Optional<Payment> findByOrderId(Long orderId);

    // Add this new method to find the most recent payment for a given order ID
    Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);
}
