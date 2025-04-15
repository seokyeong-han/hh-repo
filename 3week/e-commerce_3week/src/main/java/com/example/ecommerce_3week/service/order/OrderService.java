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

        Long orderId = order.getId();

        // OrderItem에 orderId 주입 **jpa연관관계를 주지 않아 직접 save
        List<OrderItem> itemsWithOrderId = items.stream()
                .map(item -> item.withOrderId(orderId))
                .toList();

        orderItemRepository.saveAll(itemsWithOrderId);

        return order;
    }

    public void save(Order order) {
        orderRepository.save(order);
    }
}
