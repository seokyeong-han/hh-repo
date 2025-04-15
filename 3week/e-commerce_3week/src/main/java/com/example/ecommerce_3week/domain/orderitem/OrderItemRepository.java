package com.example.ecommerce_3week.domain.orderitem;

import com.example.ecommerce_3week.infrastructure.order.OrderJpaEntity;

import java.util.List;

public interface OrderItemRepository {
    void saveAll(List<OrderItem> orderItems, OrderJpaEntity orderJpaEntity);
}
