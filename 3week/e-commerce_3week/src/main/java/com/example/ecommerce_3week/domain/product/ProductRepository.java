package com.example.ecommerce_3week.domain.product;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long id);
}
