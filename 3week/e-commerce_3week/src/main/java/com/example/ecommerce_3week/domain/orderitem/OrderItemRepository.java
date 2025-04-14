package com.example.ecommerce_3week.domain.orderitem;

import java.util.List;

public interface OrderItemRepository {
    void saveAll(List<OrderItem> orderItems);
}
