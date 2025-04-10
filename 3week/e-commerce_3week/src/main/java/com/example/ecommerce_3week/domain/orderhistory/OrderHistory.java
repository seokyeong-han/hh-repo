package com.example.ecommerce_3week.domain.orderhistory;

import java.time.LocalDateTime;

public class OrderHistory {
    private Long id;
    private Long userId;
    private Long productId;
    private int quantity;
    private Long totalPrice;
    private LocalDateTime orderedAt;

    public OrderHistory(Long userId, Long productId, int quantity, Long totalPrice) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderedAt = LocalDateTime.now();
    }
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

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