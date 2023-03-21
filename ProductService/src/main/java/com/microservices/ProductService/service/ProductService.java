package com.microservices.ProductService.service;

import com.microservices.ProductService.model.ProductRequest;
import com.microservices.ProductService.model.ProductResponse;

public interface ProductService {

	Long addProduct(ProductRequest productRequest);

	ProductResponse getProductById(long productId);

    void reduceQantity(long productId, long quantity);
}
