package com.example.ecommerce_3week.service.order;

import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.order.OrderItem;
import com.example.ecommerce_3week.domain.order.OrderRepository;
import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.dto.order.facade.OrderFacadeRequest;
import com.example.ecommerce_3week.dto.order.facade.PreparedOrderItems;
import com.example.ecommerce_3week.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public Order createOrder(User user, List<OrderItem> items) {
        Order order = new Order(user.getId(), items);

        return order;
    }

    public void save(Order order) {
        orderRepository.save(order);
    }
}
