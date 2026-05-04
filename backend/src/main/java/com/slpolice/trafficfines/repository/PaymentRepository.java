package com.slpolice.trafficfines.repository;

import com.slpolice.trafficfines.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByFine_Id(Long fineId);
    Optional<Payment> findByTransactionId(String transactionId);
}
