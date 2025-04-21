package com.example.ecommerce.domain.product.model;

public class Product {
    private Long id;
    private Long price;
    private Integer stock;


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
