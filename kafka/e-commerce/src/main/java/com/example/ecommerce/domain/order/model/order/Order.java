package com.example.ecommerce.domain.order.model.order;

import com.example.ecommerce.domain.order.entity.order.OrderJpaEntity;
import com.example.ecommerce.domain.order.model.orderItem.OrderItem;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Order {
    private Long id;
    private Long userId;
    private Long totalPrice;
    private LocalDateTime createdAt;

    public Order(Long id, Long userId, List<OrderItem> items) {
        this.id = id;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.totalPrice = items.stream()
                .mapToLong(OrderItem::getTotalPrice)
                .sum();
    }

    public Order(Long id, Long userId, Long totalPrice, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    // 정적 팩토리 메서드 (JPA Entity → Domain)
    public static Order toDomain(OrderJpaEntity entity) {
        return new Order(
                entity.getId(), 
                entity.getUserId(), 
                entity.getTotalPrice(),
                entity.getCreatedAt()
        );
    }

}
