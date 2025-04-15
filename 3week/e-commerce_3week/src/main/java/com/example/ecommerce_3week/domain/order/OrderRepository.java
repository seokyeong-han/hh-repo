package com.example.ecommerce_3week.domain.order;

import java.util.List;

public interface OrderRepository {
    void save(Order order);

    void deleteAll();

    List<Order> findAll();
}
