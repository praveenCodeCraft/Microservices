package com.microservices.OrderService.controller;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.microservices.OrderService.OrderServiceConfig;
import com.microservices.OrderService.model.OrderRequest;
import com.microservices.OrderService.model.PaymentMode;
import com.microservices.OrderService.repository.OrderRepository;
import com.microservices.OrderService.service.OrderService;
import org.hibernate.usertype.UserVersionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.util.StreamUtils.*;

@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = OrderServiceConfig.class)
public class OrderControllerTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMockServer
            = WireMockExtension.newInstance()
            .options(WireMockConfiguration
                    .wireMockConfig()
                    .port(8080))
            .build();


    private ObjectMapper objectMapper
            = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setup() throws IOException {
        getProductDetailsResponse();
        doPayment();
        getPaymentDetails();
        reduceQuantity();
    }

    private void reduceQuantity() {
        wireMockServer.stubFor(put(urlMatching("/product/reduceQuantity/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE))
        );
    }

    private void getPaymentDetails() throws IOException {
        wireMockServer.stubFor(get("/payment/.*")
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.OK.value())
                        .withBody(copyToString(OrderControllerTest.class
                                .getClassLoader()
                                .getResourceAsStream("Mock/GetPayment.json"),
                                 Charset.defaultCharset()
                                        )
                                )));
    }

    private void doPayment() {
        wireMockServer.stubFor(post("/payment")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    private void getProductDetailsResponse() throws IOException {
        //GET /product/1
        wireMockServer.stubFor(get("/product/1")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(copyToString(OrderControllerTest.class
                                .getClassLoader()
                                .getResourceAsStream("Mock/GetProduct.json"),
                                Charset.defaultCharset()
                                ))));
    }

    private OrderRequest getMockOrderRequest() {
       return  OrderRequest.builder()
                .productId(1)
                .paymentMode(PaymentMode.CASH)
                .quantity(10)
                .amount(200)
                .build();

    }

    @Test
    public void  test_WhenPlaceOrder_DoPayment_Success() throws Exception {
        //first place order
        //Get order by orderId from Db and Check
        //Check Output

        OrderRequest orderRequest = getMockOrderRequest();

        MvcResult mvcResult
                = mockMvc.perform(MockMvcRequestBuilders.post("/order/placeOrder")
                .with(jwt().authorities(new SimpleGrantedAuthority("Customer")))
                .content(objectMapper.writeValueAsString(orderRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String orderId = mvcResult.getResponse().getContentAsString();

    }



}