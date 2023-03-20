package com.microservices.ProductService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.ProductService.entity.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long>{

}
