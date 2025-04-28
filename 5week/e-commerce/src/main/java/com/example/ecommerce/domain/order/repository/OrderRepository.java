package com.example.ecommerce.domain.order.repository;

import com.example.ecommerce.domain.order.model.Order;

import java.util.List;

public interface OrderRepository {
    List<Order> findAll();

    Order save(Order order);
}
