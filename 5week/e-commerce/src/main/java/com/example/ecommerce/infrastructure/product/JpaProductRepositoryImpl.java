package com.example.ecommerce.infrastructure.product;

import com.example.ecommerce.domain.product.entity.ProductJpaEntity;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
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
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(Product::toDomain)
                .toList();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id)
                .map(Product::toDomain);
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity saved = jpaRepository.save(ProductJpaEntity.fromDomain(product));
        return Product.toDomain(saved);
    }

    public void saveAll(List<Product> products) {
        List<ProductJpaEntity> entities = products.stream()
                .map(ProductJpaEntity::fromDomain)
                .collect(Collectors.toList());
        jpaRepository.saveAll(entities); // JPA의 실제 save 호출
    }


}
