package com.example.ecommerce.domain.order.entity;

import com.example.ecommerce.domain.order.model.Order;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long totalPrice;
    private LocalDateTime createdAt;

    protected OrderJpaEntity() {}

    public OrderJpaEntity(Long id, Long userId, Long totalPrice, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    // 도메인 → JPA 변환
    public static OrderJpaEntity fromDomain(Order order) {
        return new OrderJpaEntity(
                order.getId(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }

    // JPA → 도메인 변환
    public Order toDomain() {
        return new Order(id, userId, totalPrice, createdAt);
    }
}
