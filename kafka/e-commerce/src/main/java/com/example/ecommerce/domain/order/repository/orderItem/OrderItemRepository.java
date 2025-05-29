package com.example.ecommerce.domain.order.repository.orderItem;

import com.example.ecommerce.domain.order.model.orderItem.OrderItem;

import java.util.List;

public interface OrderItemRepository {
    List<OrderItem> saveAll(List<OrderItem> items, Long orderId);
    void deleteByOrderId(Long orderId);
}
