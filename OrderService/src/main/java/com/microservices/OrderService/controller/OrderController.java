package com.microservices.OrderService.controller;

import com.microservices.OrderService.entity.Order;
import com.microservices.OrderService.model.OrderRequest;
import com.microservices.OrderService.model.OrderResponse;
import com.microservices.OrderService.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/order")
@Log4j2
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAuthority('Customer')")
    @PostMapping("/placeOrder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {
        long orderId = orderService.placeOrder(orderRequest);
        log.info("oder id is : {}", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer') || hasAuthority('SCOPE_internal')")
    @GetMapping("{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable long orderId){
        log.info("order id is : {} " , orderId);
        OrderResponse orderResponse = orderService.getOrder(orderId);
        return new ResponseEntity<>(orderResponse,HttpStatus.OK);
    }
    }


