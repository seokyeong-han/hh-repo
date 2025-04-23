package com.example.ecommerce.domain.order.repository;

import com.example.ecommerce.domain.order.model.Order;

public interface OrderRepository {
    Order save(Order order);
}
