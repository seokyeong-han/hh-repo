package com.example.ecommerce.domain.order.model.orderItem;

import com.example.ecommerce.domain.order.entity.orderItem.OrderItemJpaEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderItem {
    private Long id;
    private Long orderId; // 외래키지만 연관관계 X
    private Long productId;
    private int quantity;
    private Long pricePerItem;
    private Long totalPrice;
    private LocalDateTime createdAt;

    public OrderItem(Long id, Long orderId, Long productId, int quantity,
                     Long totalPrice, Long pricePerItem, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.pricePerItem = pricePerItem;
        this.createdAt = createdAt;
    }

    // 정적 팩토리 메서드 (JPA Entity → Domain)
    public static OrderItem toDomain(OrderItemJpaEntity entity) {
        return new OrderItem(
                entity.getId(),
                entity.getOrderId(),
                entity.getProductId(),
                entity.getQuantity(),
                entity.getPricePerItem(),
                entity.getTotalPrice(),
                entity.getCreatedAt()
        );
    }
}
