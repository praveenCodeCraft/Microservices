package com.microservices.OrderService.service;

import com.microservices.OrderService.model.OrderRequest;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);
}
