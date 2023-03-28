package com.microservices.PaymentService.service;

import com.microservices.PaymentService.model.PaymentRequest;
import com.microservices.PaymentService.model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetails(Long orderId);
}
