package com.microservices.OrderService.service;

import com.microservices.OrderService.entity.Order;
import com.microservices.OrderService.exception.CustomException;
import com.microservices.OrderService.external.client.PaymentService;
import com.microservices.OrderService.external.client.ProductService;
import com.microservices.OrderService.model.*;
import com.microservices.OrderService.repository.OrderRepository;
import com.microservices.OrderService.request.PaymentRequest;
import com.netflix.discovery.converters.Auto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService ;

    @Autowired
    private RestTemplate restTemplate ;

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

    @Override
    public OrderResponse getOrder(long orderId) {
        log.info("Order Id for get order is {}", orderId);

        Order order
                = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("The order is not found for this order", "ORDER_NOT_FOUND", 404));

      ProductResponse productResponse =  restTemplate.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId() , ProductResponse.class );

      PaymentResponse payementResponse  = restTemplate.getForObject("http://PAYMENT-SERVICE/payment/" + order.getId() , PaymentResponse.class) ;

      OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
              .productName(productResponse.getProductName())
              .productId(productResponse.getProductId())
              .quantity(productResponse.getQuantity())
              .price(productResponse.getPrice())
              .build();


      OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
              .paymentId(payementResponse.getPaymentId())
              .paymentDate(payementResponse.getPaymentDate())
              .status(payementResponse.getStatus())
              .paymentMode(payementResponse.getPaymentMode())
              .build();


        OrderResponse orderResponse = OrderResponse.builder()
                .amount(order.getAmount())
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();


        log.info("Order details for the given order Id is : {}" , orderResponse);

        return orderResponse;
    }


}
