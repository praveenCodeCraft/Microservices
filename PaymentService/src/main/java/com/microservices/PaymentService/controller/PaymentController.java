package com.microservices.PaymentService.controller;

import com.microservices.PaymentService.model.PaymentRequest;
import com.microservices.PaymentService.model.PaymentResponse;
import com.microservices.PaymentService.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @PostMapping("/doPayment")
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest){
    paymentService.doPayment(paymentRequest);
    return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetails(@PathVariable Long orderId){
        PaymentResponse paymentResponse = paymentService.getPaymentDetails(orderId);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }

}
