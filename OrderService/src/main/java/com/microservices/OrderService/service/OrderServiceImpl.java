package com.microservices.OrderService.service;

import com.microservices.OrderService.entity.Order;
import com.microservices.OrderService.model.OrderRequest;
import com.microservices.OrderService.repository.OrderRepository;
import com.netflix.discovery.converters.Auto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Override
    public long placeOrder(OrderRequest orderRequest) {
    log.info("The order Request is : {}", orderRequest);
    Order order = Order.builder()
            .amount(orderRequest.getAmount())
            .quantity(orderRequest.getQuantity())
            .productId(orderRequest.getProductId())
            .orderStatus("CREATED")
            .orderDate(Instant.now())
            .build();

        order = orderRepository.save(order);
        log.info("Order id is : {}", order.getId());
        return order.getId();
    }

    public static void main(String[] args) {
        int numRay[] = { 0, 4, 3, 2, 7, 8, 2, 3, 1 };
System.out.println(3%9);
        for (int i = 0; i < numRay.length; i++) {

            numRay[numRay[i] % numRay.length]
                    = numRay[numRay[i] % numRay.length]
                    + numRay.length;
            System.out.println("ytrewq " + i + ": " +  numRay[numRay[i] % numRay.length]
                   );

        }

        for (int i = 0; i < numRay.length; i++) {
//            System.out.println("ytrewq " + numRay[i]);
            if (numRay[i] >= numRay.length * 2) {
                System.out.println(i + " ");
            }
        }
    }
}
