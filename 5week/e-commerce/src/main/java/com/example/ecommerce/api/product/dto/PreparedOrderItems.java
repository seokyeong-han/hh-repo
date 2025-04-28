package com.example.ecommerce.api.product.dto;

import com.example.ecommerce.domain.order.model.OrderItem;

import java.util.List;

public class PreparedOrderItems {
    private final List<OrderItem> orderItems; // 각 상품에 대한 주문 정보

    public PreparedOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    public long getTotalPrice() {
        return orderItems.stream()
                .mapToLong(OrderItem::getTotalPrice)
                .sum();
    }
}
