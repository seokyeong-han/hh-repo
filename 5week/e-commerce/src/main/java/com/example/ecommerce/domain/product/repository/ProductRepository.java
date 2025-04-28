package com.example.ecommerce.domain.product.repository;

import com.example.ecommerce.domain.product.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    void saveAll(List<Product> products);
    Product save(Product product);

    Optional<Product> findWithPessimisticLockById(Long id);
}
