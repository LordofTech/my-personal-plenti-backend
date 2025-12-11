package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByTransactionReference(String transactionReference);
    List<PaymentTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<PaymentTransaction> findByOrderId(Long orderId);
}
