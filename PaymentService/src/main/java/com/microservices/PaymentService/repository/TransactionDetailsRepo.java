package com.microservices.PaymentService.repository;

import com.microservices.PaymentService.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDetailsRepo extends JpaRepository<TransactionDetails, Long> {

    TransactionDetails findByOrderId(Long orderId);
}
