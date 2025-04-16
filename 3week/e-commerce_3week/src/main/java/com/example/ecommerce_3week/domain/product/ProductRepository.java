package com.example.ecommerce_3week.domain.product;

import com.example.ecommerce_3week.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long id);
    Product save(Product product);

    void deleteAll();

    List<Product> findAll();
}
