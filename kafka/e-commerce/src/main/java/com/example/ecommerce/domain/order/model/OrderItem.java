package com.example.ecommerce.domain.order.model;

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
}
