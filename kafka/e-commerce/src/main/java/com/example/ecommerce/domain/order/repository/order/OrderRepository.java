package com.example.ecommerce.domain.order.repository.order;

import com.example.ecommerce.domain.order.model.order.Order;

import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long id);
    Order save(Order order);
    void deleteByOrderId(Long orderId);
}
