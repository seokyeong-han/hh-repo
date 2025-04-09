package com.example.ecommerce_3week.service.product;

import com.example.ecommerce_3week.domain.order.OrderItem;
import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.product.ProductRepository;
import com.example.ecommerce_3week.dto.order.controller.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    //조회
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    public void save(List<Product> products) {
        for (Product product : products) {
            productRepository.save(product);
        }
    }



}
