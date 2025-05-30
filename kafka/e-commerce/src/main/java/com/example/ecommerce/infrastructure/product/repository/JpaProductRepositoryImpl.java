package com.example.ecommerce.infrastructure.product.repository;

import com.example.ecommerce.domain.product.entity.ProductJpaEntity;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
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
                .map(Product::toDomain);
    }

    //비관적 락 조회
    @Override
    public Optional<Product> findWithPessimisticLockById(Long id) {
        return jpaRepository.findWithPessimisticLockById(id)
                .map(Product::toDomain);
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity saved = jpaRepository.save(ProductJpaEntity.fromDomain(product));
        return Product.toDomain(saved);
    }
}
