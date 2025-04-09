package com.example.ecommerce_3week.service.product;

import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    //조회
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    //주문


}
