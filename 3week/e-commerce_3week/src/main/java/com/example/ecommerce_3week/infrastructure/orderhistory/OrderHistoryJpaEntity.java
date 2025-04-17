package com.example.ecommerce_3week.infrastructure.orderhistory;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString // Lombok의 @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderHistoryJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long userId;
    private Long productId;
    private int quantity;
    private Long totalPrice;
    private LocalDateTime orderedAt;

    public OrderHistoryJpaEntity(Long userId, Long orderId, Long productId, int quantity, Long totalPrice) {
        this.userId = userId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderedAt = LocalDateTime.now();
    }
}
