package com.example.ecommerce_3week.domain.orderhistory;

import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import com.example.ecommerce_3week.infrastructure.orderhistory.OrderHistoryJpaEntity;

import java.time.LocalDateTime;

public class OrderHistory {
    private Long id;
    private Long userId;
    private Long orderId;
    private Long productId;
    private int quantity;
    private Long totalPrice;
    private LocalDateTime orderedAt;

    // 저장용 생성자
    public OrderHistory(Long userId, Long orderId, Long productId, int quantity, Long totalPrice) {
        this.userId = userId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderedAt = LocalDateTime.now();
    }

    public OrderHistory(Long id, Long orderId, Long userId, Long productId, int quantity, Long totalPrice, LocalDateTime orderedAt) {
        this.id = id;
        this.userId = userId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderedAt = LocalDateTime.now();
    }

    public static OrderHistory toDomain(OrderHistoryJpaEntity entity) {
        return new OrderHistory(
                entity.getId(),
                entity.getOrderId(),
                entity.getUserId(),
                entity.getProductId(),
                entity.getQuantity(),
                entity.getTotalPrice(),
                entity.getOrderedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOrderId() {return orderId;}

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }


}