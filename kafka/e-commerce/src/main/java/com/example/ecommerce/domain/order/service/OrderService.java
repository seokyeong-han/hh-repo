package com.example.ecommerce.domain.order.service;

import com.example.ecommerce.api.order.dto.ProductOrderItem;
import com.example.ecommerce.domain.order.model.Order;
import com.example.ecommerce.domain.order.model.OrderItem;
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

    @Transactional
    public void placeOrder(Long userId, List<ProductOrderItemMessage> items){
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



    }
}
