package com.microservices.PaymentService.service;

import com.microservices.PaymentService.entity.TransactionDetails;
import com.microservices.PaymentService.model.PaymentMode;
import com.microservices.PaymentService.model.PaymentRequest;
import com.microservices.PaymentService.model.PaymentResponse;
import com.microservices.PaymentService.repository.TransactionDetailsRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private TransactionDetailsRepo transactionDetailsRepo;
    @Override
    public long doPayment(PaymentRequest paymentRequest) {
        log.info("Payment Detail is {}", paymentRequest);

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .amount(paymentRequest.getAmount())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .paymentDate(Instant.now())
                .paymentStatus("SUCCESS")
                .orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .build();

        transactionDetailsRepo.save(transactionDetails);
        log.info("TransactionDetails id is {}", transactionDetails.getId());
        return transactionDetails.getId() ;
    }

    @Override
    public PaymentResponse getPaymentDetails(Long orderId) {

        log.info("The orderId for the payment is : {}" , orderId);
        TransactionDetails transactionDetails = transactionDetailsRepo.findByOrderId(orderId);
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentId(transactionDetails.getId())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .status(transactionDetails.getPaymentStatus())
                .amount(transactionDetails.getAmount())
                .paymentDate(transactionDetails.getPaymentDate())
                .orderId(transactionDetails.getOrderId())
                .build();

        return paymentResponse;
    }
}
