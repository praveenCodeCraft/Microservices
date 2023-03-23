package com.microservices.OrderService.external.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.OrderService.exception.CustomException;
import com.microservices.OrderService.response.ErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {

        ObjectMapper objectMapper = new ObjectMapper();

        log.info("response url is {}", response.request().url());
        log.info("response header is {}", response.request().headers());

        try {
            ErrorResponse errorResponse = objectMapper.readValue(response.body().asInputStream(),ErrorResponse.class);
            return  new CustomException(errorResponse.getErrorMessage(),errorResponse.getErrorCode(),response.status());
        } catch (IOException e) {
            return new CustomException("Internal Server Error", "INTERNAL_SERVER_ERROR", 500);
        }

    }
}
