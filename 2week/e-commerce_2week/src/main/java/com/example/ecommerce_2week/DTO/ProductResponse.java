package com.example.ecommerce_2week.DTO;

import com.example.ecommerce_2week.entity.Product;
import lombok.Getter;

@Getter
public class ProductResponse {
    private Long id;
    private String productName;
    private Long price;
    private int stock;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.stock = product.getStock();
    }
}
