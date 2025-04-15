package com.example.ecommerce_3week.service.order;

import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import com.example.ecommerce_3week.domain.orderitem.OrderItemRepository;
import com.example.ecommerce_3week.domain.order.OrderRepository;
import com.example.ecommerce_3week.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Order createOrder(User user, List<OrderItem> items) {
        Order order = new Order(user.getId(), items); //total가격 저장
        save(order);

        return order;
    }

    public void save(Order order) {
        orderRepository.save(order);
    }
}
