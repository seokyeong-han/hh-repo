package com.example.ecommerce_3week.dto.product.contorller;

import com.example.ecommerce_3week.dto.product.facade.ProductFacadeResponse;
import lombok.Getter;

@Getter
public class ProductResponse {
    //contorller -> facade
    private Long id;
    private Long price;
    private Integer stock;

    public ProductResponse(ProductFacadeResponse response) {
        this.id = response.getId();
        this.price = response.getPrice();
        this.stock = response.getStock();
    }

}
