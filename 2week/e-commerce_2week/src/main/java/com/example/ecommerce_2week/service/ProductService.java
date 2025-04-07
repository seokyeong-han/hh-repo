package com.example.ecommerce_2week.service;

import com.example.ecommerce_2week.DTO.ProductResponse;
import com.example.ecommerce_2week.entity.Product;
import com.example.ecommerce_2week.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private static final String PRODUCT_NOT_FOUND_MSG = "상품을 찾을 수 없습니다.";

    @Autowired
    ProductRepository productRepository;

    public Product getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(PRODUCT_NOT_FOUND_MSG));
        return product;
    }
}
