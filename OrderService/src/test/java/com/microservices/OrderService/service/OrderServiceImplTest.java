package com.microservices.OrderService.service;

import com.microservices.OrderService.entity.Order;
import com.microservices.OrderService.exception.CustomException;
import com.microservices.OrderService.external.client.PaymentService;
import com.microservices.OrderService.external.client.ProductService;
import com.microservices.OrderService.model.*;
import com.microservices.OrderService.repository.OrderRepository;
import com.microservices.OrderService.request.PaymentRequest;
import com.thoughtworks.xstream.mapper.Mapper;
import org.hibernate.sql.ordering.antlr.OrderByTemplateTokenTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
//import static org.mockito.Mockito;

@SpringBootTest
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService ;

    @Mock
    private RestTemplate restTemplate ;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl() ;

    @DisplayName("Get order - Success Scenario")
    @Test
    public void test_When_Order_success () {
        Order order  = getMockOrder();
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        when(restTemplate.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId() , ProductResponse.class ))
                .thenReturn(getMockProductResponse());

        when(restTemplate.getForObject("http://PAYMENT-SERVICE/payment/" + order.getId() , PaymentResponse.class) )
                .thenReturn(getMockPaymentResponse());

       //Actual
        OrderResponse orderResponse = orderService.getOrder(1);

        //Verification(verifying that particular call happens or not )
        verify(orderRepository,times(1)).findById(anyLong());
        verify(restTemplate,times(1)).getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId()
                , ProductResponse.class );
        verify(restTemplate,times(1)).getForObject("http://PAYMENT-SERVICE/payment/" + order.getId() 
                , PaymentResponse.class) ;

        //Assertion
        assertNotNull(orderResponse);
        assertEquals(order.getId() , orderResponse.getOrderId());

    }

    @DisplayName("Get Order - Failure scenario")
    @Test
    void test_When_Get_Order_Not_Found_Than_Not_Found(){

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

//        OrderResponse orderResponse = orderService.getOrder(1) ;

        CustomException customException =
                assertThrows(CustomException.class,
        () -> orderService.getOrder(1));
        assertEquals("ORDER_NOT_FOUND" , customException.getErrorCode());
        assertEquals(404 , customException.getStatus());

        verify(orderRepository,times(1)).findById(anyLong());


    }
    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_When_place_order_success(){

        Order order  = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();


        //Testing
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));

        //Actual
        long orderId= orderService.placeOrder(orderRequest);

        //verification
        verify(orderRepository,times(2))
                .save(any());
        verify(productService,times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService,times(1))
                .doPayment(any(PaymentRequest.class));

        //Assertion
        assertEquals(order.getId() , orderId);

    }

    @DisplayName("Place Order - Payment fails scenario")
    @Test
    void test_When_Place_Order_Payment_Fails_then_Order_Placed(){

        Order order  = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();


        //Testing
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        //Actual
        long orderId= orderService.placeOrder(orderRequest);

        verify(orderRepository,times(2))
                .save(any());
        verify(productService,times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService,times(1))
                .doPayment(any(PaymentRequest.class));

        //Assertion
        assertEquals(order.getId() , orderId);
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .quantity(10)
                .amount(100)
                .paymentMode(PaymentMode.CASH)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .orderId(1)
                .paymentId(1)
                .paymentMode(PaymentMode.CASH)
                .status("Accepted")
                .amount(200)
                .paymentDate(Instant.now())
                .build();
    }

    private  ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(2)
                .productName("iphone")
                .price(100)
                .build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .id(1)
                .amount(100)
                .productId(2)
                .quantity(200)
                .build();
    }

}