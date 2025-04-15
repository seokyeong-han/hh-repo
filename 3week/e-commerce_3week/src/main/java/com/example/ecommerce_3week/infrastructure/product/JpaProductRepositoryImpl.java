package com.example.ecommerce_3week.infrastructure.product;

import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository jpaRepository;

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id)
                .map(Product::toDomain);
    }

    @Override
    public void save(Product product) {
        jpaRepository.save(toEntity(product));
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(Product::toDomain)
                .collect(Collectors.toList());
    }

    private ProductJpaEntity toEntity(Product product) {
        return new ProductJpaEntity(product.getId(), product.getPrice(), product.getStock());
    }
}
