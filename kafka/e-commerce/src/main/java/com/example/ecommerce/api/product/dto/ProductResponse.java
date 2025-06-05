package com.example.ecommerce.api.product.dto;

import com.example.ecommerce.domain.product.model.Product;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProductResponse {
    private Long id;
    private String name;
    private Long price;
    private Integer stock;

    //생성자
    public ProductResponse(Long id, String name, Long price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    // 단일 Product -> ProductResponse
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock()
        );
    }

    //List<Product> -> List<ProductResponse>
    public static List<ProductResponse> from(List<Product> products) {
        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }



}
