package com.example.ecommerce.domain.order.service;

import com.example.ecommerce.domain.order.model.order.Order;
import com.example.ecommerce.domain.order.model.orderItem.OrderItem;
import com.example.ecommerce.domain.order.repository.orderItem.OrderItemRepository;
import com.example.ecommerce.domain.order.repository.order.OrderRepository;
import com.example.ecommerce.domain.product.dto.ProductOrderItemMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    @Transactional
    public Long placeOrder(Long userId, List<ProductOrderItemMessage> items){
        List<OrderItem> orderItems = new ArrayList<>();
        long totalPrice = 0L; //주문별 총 가격
        for (ProductOrderItemMessage item : items) {
            orderItems.add(new OrderItem(
                    null, null, userId,  // orderId는 아직 없으므로 null
                    item.getQuantity(), item.getTotalPrice(), item.getPricePerItem(), //ProductOrderItem이거 생성할때 금액 계산 하기
                    LocalDateTime.now()
            ));
            totalPrice += item.getTotalPrice();
        }

        Order order = new Order(null, userId, orderItems);
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems, savedOrder.getId());

        return totalPrice;

    }
}
