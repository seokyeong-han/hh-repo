package com.example.ecommerce_3week.infrastructure.product;

import com.example.ecommerce_3week.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaProductRepositoryImpl implements ProductRepository {
    private final ProductRepository productRepository;


}
