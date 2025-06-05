package com.example.ecommerce.domain.product.repository;

import com.example.ecommerce.domain.product.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    long count();
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Optional<Product> findWithPessimisticLockById(Long id);

    Product save(Product product);
    void saveAll(List<Product> products);
}
