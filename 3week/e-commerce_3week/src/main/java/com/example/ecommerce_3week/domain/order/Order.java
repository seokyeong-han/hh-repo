package com.example.ecommerce_3week.domain.order;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private Long userId;
    private List<OrderItem> items;
    private Long totalPrice;
    private LocalDateTime createdAt;

    public Order(Long userId, List<OrderItem> items) {
        this.userId = userId;
        this.items = items;
        this.totalPrice = calculateTotalPrice(items);
        this.createdAt = LocalDateTime.now();
    }

    private Long calculateTotalPrice(List<OrderItem> items) { //주문상품 총 가격
        return items.stream()
                .mapToLong(OrderItem::getTotalPrice)
                .sum();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}