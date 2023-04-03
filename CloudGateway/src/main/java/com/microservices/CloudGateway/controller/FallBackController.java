package com.microservices.CloudGateway.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class FallBackController {

    @GetMapping("/orderServiceFallBack")
    public String orderServiceFallBack(){
        return "order Service Is Down !" ;
    }

    @GetMapping("/productServiceFallBack")
    public String productServiceFallBack(){
        return "Product Service Is Down !" ;
    }

    @GetMapping("/paymentServiceFallBack")
    public String paymentServiceFallBack(){
        return "Payment Service Is Down !" ;
    }
}
