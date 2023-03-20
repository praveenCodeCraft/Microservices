package com.microservices.OrderService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private long productId ;
    private long quantity;
    private long amount;

    private PaymentMode paymentMode;
}
