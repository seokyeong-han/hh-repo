package com.example.ecommerce.domain.product.model;

import com.example.ecommerce.domain.product.entity.ProductJpaEntity;

public class Product {
    private Long id;
    private Long price;
    private Integer stock;

    // 생성자
    public Product(Long id, Long price, Integer stock) {
        this.id = id;
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
        return new Product(entity.getId(), entity.getPrice(), entity.getStock());
    }

    //getter
    public Long getId() {
        return id;
    }
    public Long getPrice() {
        return price;
    }
    public Integer getStock() {
        return stock;
    }
}
