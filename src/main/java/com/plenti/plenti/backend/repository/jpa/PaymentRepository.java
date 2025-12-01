package com.plenti.plenti.backend.repository;

import com.plenti.plenti.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Payment findByReference(String reference);
}
