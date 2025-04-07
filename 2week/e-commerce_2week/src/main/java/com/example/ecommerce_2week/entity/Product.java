package com.example.ecommerce_2week.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 고유 ID

    @Column(nullable = false, length = 100)
    private String productName; // 상품명

    @Column(nullable = false)
    private Long price; // 가격

    @Column(nullable = false)
    private int stock; // 재고

    //재고 추가, 감소 메서드
    public void updateStock(int quantity) {
        if (this.stock + quantity < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stock += quantity;
    }
}
