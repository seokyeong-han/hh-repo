package com.example.ecommerce.domain.product.repository;

import com.example.ecommerce.domain.product.model.Product;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long id);
    Optional<Product> findWithPessimisticLockById(Long id);

    Product save(Product product);
}
