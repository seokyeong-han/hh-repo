package com.example.ecommerce.domain.order.repository.order;

import com.example.ecommerce.domain.order.model.order.Order;

public interface OrderRepository {
    Order save(Order order);
}
