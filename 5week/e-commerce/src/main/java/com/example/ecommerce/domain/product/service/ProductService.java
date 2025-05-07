package com.example.ecommerce.domain.product.service;

import com.example.ecommerce.api.order.dto.OrderCommand;
import com.example.ecommerce.api.product.dto.PreparedOrderItems;
import com.example.ecommerce.domain.order.model.OrderItem;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    //전체 상품 조회
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    //단일 상품 조회
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    public void saveAll(List<Product> products) {
        productRepository.saveAll(products);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    //재고 롤백
    @Transactional
    public void rollbackStock(List<OrderItem> orderItems) { // 트랜잭션 내에서 롤백 처리
        for (OrderItem item : orderItems) {
            Product product = findById(item.getProductId());
            product.restoreStock(item.getQuantity());
            productRepository.save(product);
        }
    }


}
