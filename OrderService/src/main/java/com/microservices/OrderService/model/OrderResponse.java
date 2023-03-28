package com.microservices.OrderService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private long orderId ;
    private Instant orderDate;
    private String orderStatus;
    private long amount ;

    private ProductDetails productDetails;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private  PaymentDetails paymentDetails;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder

    public static class ProductDetails {

        private String productName ;
        private  long productId;
        private long quantity;
        private long price;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder

    public static class PaymentDetails {

        private long paymentId;
        private String status;
        private PaymentMode paymentMode;
        @JsonIgnore
        private long amount ;
        private Instant paymentDate ;

    }
}
