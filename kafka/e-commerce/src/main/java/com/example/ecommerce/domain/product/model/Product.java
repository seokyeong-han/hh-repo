package com.example.ecommerce.domain.product.model;

import com.example.ecommerce.domain.product.entity.ProductJpaEntity;
import lombok.Getter;

@Getter
public class Product {
    private Long id;
    private String name;
    private Long price;
    private Integer stock;

    public Product(Long id, String name, Long price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    //재고차감
    public void deductStock(int quantity) {
        if (stock < quantity) {
            throw new IllegalArgumentException("상품 재고가 부족합니다.");
        }
        this.stock -= quantity;
    }

    //재고롤백
    public void restoreStock(int quantity) {
        this.stock += quantity;
    }


    // 정적 팩토리 메서드 (JPA Entity → Domain)
    public static Product toDomain(ProductJpaEntity entity) {
        return new Product(entity.getId(), entity.getName(), entity.getPrice(), entity.getStock());
    }
}
