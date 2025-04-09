package com.example.ecommerce_3week.domain.product;

public class Product {
    private Long id;
    private Long price;
    private Integer stock;

    public Product(Long id, Long price, Integer stock) {
        this.id = id;
        this.price = price;
        this.stock = stock;
    }

    public void deductStock(int quantity) {
        if (stock < quantity) {
            throw new IllegalArgumentException("상품 재고가 부족합니다.");
        }
        this.stock -= quantity;
    }

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
