package com.microservices.OrderService.service;

import com.microservices.OrderService.entity.Order;
import com.microservices.OrderService.exception.CustomException;
import com.microservices.OrderService.external.client.PaymentService;
import com.microservices.OrderService.external.client.ProductService;
import com.microservices.OrderService.model.OrderRequest;
import com.microservices.OrderService.repository.OrderRepository;
import com.microservices.OrderService.request.PaymentRequest;
import com.netflix.discovery.converters.Auto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService ;

    @Autowired
    private PaymentService paymentService;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
    log.info("The order Request is : {}", orderRequest);

    productService.reduceQuantity(orderRequest.getProductId(),orderRequest.getQuantity());
    log.info("Order is created .");
    Order order = Order.builder()
            .amount(orderRequest.getAmount())
            .quantity(orderRequest.getQuantity())
            .productId(orderRequest.getProductId())
            .orderStatus("CREATED")
            .orderDate(Instant.now())
            .build();


        order = orderRepository.save(order);

        log.info("Calling payment service to complete the payment.");

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .amount(orderRequest.getAmount())
                .paymentMode(orderRequest.getPaymentMode())
                .build();

        String orderStatus = null;

        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment Done Successfully , changing orderStatus null to placed");
            orderStatus = "PLACED" ;
        }catch (Exception e){

            log.info("Exception is {}" ,e);
            log.info("Payment Is Failed , changing orderStatus null to failed");
            orderStatus = "Payment Failed" ;
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order id is : {}", order.getId());
        return order.getId();
    }


}
