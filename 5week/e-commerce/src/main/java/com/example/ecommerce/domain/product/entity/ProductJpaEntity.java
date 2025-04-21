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

    private Long price;

    private Integer stock;

    //기본 생성자
    public ProductJpaEntity(Long id, Long price, Integer stock) {
        this.id = id;
        this.price = price;
        this.stock = stock;
    }

    public static ProductJpaEntity fromDomain(Product domain) {
        return new ProductJpaEntity(
                domain.getId(),//새로 생성하는것이 아닌 기존데이터를 업데이트 하는거라 id 포함
                domain.getPrice(),
                domain.getStock()
        );
    }
}
