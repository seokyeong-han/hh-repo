package com.example.ecommerce.domain.order.model;

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

}
