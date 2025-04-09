package com.example.ecommerce_3week.infrastructure.product;

import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository jpaRepository;

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    private Product toDomain(ProductJpaEntity entity) {
        return new Product(entity.getId(), entity.getPrice(), entity.getStock());
    }


}
