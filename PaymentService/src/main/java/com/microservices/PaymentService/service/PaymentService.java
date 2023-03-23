package com.microservices.PaymentService.service;

import com.microservices.PaymentService.model.PaymentRequest;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);
}
