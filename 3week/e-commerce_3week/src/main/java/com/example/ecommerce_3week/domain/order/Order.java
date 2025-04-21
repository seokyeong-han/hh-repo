package com.example.ecommerce_3week.domain.order;

import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import com.example.ecommerce_3week.infrastructure.order.OrderJpaEntity;
import org.aspectj.weaver.ast.Or;

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

        for (OrderItem item : items) {
            item.assignOrder(this);
        }
    }

    public Order(Long id, Long userId, Long totalPrice, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    private Long calculateTotalPrice(List<OrderItem> items) { //주문상품 총 가격
        return items.stream()
                .mapToLong(OrderItem::getTotalPrice)
                .sum();
    }

    public static Order toDomain(OrderJpaEntity entity) {
        return new Order(
                entity.getId(),
                entity.getUserId(),
                entity.getTotalPrice(),
                entity.getCreatedAt()
        );
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