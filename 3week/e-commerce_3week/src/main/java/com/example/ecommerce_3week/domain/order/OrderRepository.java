package com.example.ecommerce_3week.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long id);

    Order save(Order order);

    void deleteAll();
}
