package com.example.ecommerce_3week.infrastructure.product;

import com.example.ecommerce_3week.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

}
