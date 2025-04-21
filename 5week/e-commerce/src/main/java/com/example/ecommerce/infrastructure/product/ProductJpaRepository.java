package com.example.ecommerce.infrastructure.product;

import com.example.ecommerce.domain.product.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {
}
