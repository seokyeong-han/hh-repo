package com.example.ecommerce.domain.order.repository;

import com.example.ecommerce.domain.order.model.OrderItem;

import java.util.List;

public interface OrderItemRepository {
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> saveAll(List<OrderItem> orderItems, Long orderId);
}
