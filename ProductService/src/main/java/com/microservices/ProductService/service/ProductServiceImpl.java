package com.microservices.ProductService.service;


import com.microservices.ProductService.exception.ProductServiceCustomException;
import com.microservices.ProductService.model.ProductResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservices.ProductService.entity.Product;
import com.microservices.ProductService.model.ProductRequest;
import com.microservices.ProductService.repository.ProductRepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {


    @Autowired
    private ProductRepo productRepo;

    @Override
    public Long addProduct(ProductRequest productRequest) {
        log.info("product request is {}", productRequest);
        Product product = Product.builder()
                .productName(productRequest.getName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();
        productRepo.save(product);
        log.info("Product is Created");
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException("The product is not found for this id","PRODUCT_NOT_FOUND"));

        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        return productResponse;
    }

    @Override
    public void reduceQantity(long productId, long quantity) {
        log.info("Reduce quantity is {} for id {}", quantity , productId);
      Product product = productRepo.findById(productId)
              .orElseThrow(() -> new ProductServiceCustomException
                      ("Product is not found for this id " , "PRODUCT_NOT_FOUND"));
        log.info("Procuct detail is {}" , product);
        if (product.getQuantity()  < quantity){
                throw new ProductServiceCustomException
                        ("Product does not have sufficient quantity","INSUFFICIENT_QUANTITY");
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepo.save(product);
        log.info("Product Quantity updated successfully. ");


    }

}
