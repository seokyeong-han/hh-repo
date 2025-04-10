package com.example.ecommerce_3week.dto.product.facade;

import lombok.Getter;

@Getter
public class ProductFacadeResponse {
    private Long id;
    private Long price;
    private Integer stock;

    public ProductFacadeResponse(Long id, Long price, Integer stock) {
        this.id = id;
        this.price = price;
        this.stock = stock;
    }
}
