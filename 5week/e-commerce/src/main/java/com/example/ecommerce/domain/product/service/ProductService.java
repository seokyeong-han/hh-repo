package com.example.ecommerce.domain.product.service;

import com.example.ecommerce.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    //전체 상품 조회
    public List<Product> findAll() {
        return null;
    }

    //단일 상품 조회
    public Product findById(Long id) {
        return null;
    }
}
