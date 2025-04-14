package com.example.ecommerce_3week.infrastructure.orderhistory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderHistoryJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long productId;
    private int quantity;
    private Long totalPrice;
    private LocalDateTime orderedAt;

    public OrderHistoryJpaEntity(Long userId, Long productId, int quantity, Long totalPrice) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderedAt = LocalDateTime.now();
    }
}
