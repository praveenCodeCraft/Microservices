package com.microservices.OrderService.service;

import com.microservices.OrderService.model.OrderRequest;
import com.microservices.OrderService.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrder(long orderId);
}
