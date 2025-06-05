package com.example.ecommerce.domain.product.service;

import com.example.ecommerce.common.cache.CacheConstants;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public static final String PRODUCT_DETAIL = CacheConstants.PRODUCT_DETAIL;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Cacheable(cacheNames = PRODUCT_DETAIL, key = "#id")
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }
}
