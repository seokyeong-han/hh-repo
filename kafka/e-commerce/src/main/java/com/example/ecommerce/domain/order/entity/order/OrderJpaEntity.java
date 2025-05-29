package com.example.ecommerce.domain.order.entity.order;

import com.example.ecommerce.domain.order.model.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "orders")
@RequiredArgsConstructor
public class OrderJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //private String orderId;
    private Long userId;
    private Long totalPrice;
    private LocalDateTime createdAt;

    public OrderJpaEntity(Long id, Long userId, Long totalPrice, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public static OrderJpaEntity fromDomain(Order domain) {
        return new OrderJpaEntity(
                domain.getId(),
                domain.getUserId(),
                domain.getTotalPrice(),
                domain.getCreatedAt()
        );
    }

}
