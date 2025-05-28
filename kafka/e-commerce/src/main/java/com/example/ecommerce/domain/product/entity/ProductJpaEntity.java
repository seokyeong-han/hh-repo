package com.example.ecommerce.domain.product.entity;

import com.example.ecommerce.domain.product.model.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@RequiredArgsConstructor
@Table(name = "product")
public class ProductJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long price;
    private Integer stock;

    //기본 생성자
    public ProductJpaEntity(Long id, String name, Long price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static ProductJpaEntity fromDomain(Product domain) {
        return new ProductJpaEntity(
                domain.getId(),
                domain.getName(),
                domain.getPrice(),
                domain.getStock()
        );
    }

}
