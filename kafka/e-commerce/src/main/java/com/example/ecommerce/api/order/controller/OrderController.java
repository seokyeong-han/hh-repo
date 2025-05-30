package com.example.ecommerce.api.order.controller;

import com.example.ecommerce.api.order.dto.CreateOrderRequest;
import com.example.ecommerce.api.order.dto.ProductOrderItem;
import com.example.ecommerce.domain.event.OrderStartEvent;
import com.example.ecommerce.infrastructure.order.producer.OrderProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderProducer orderProducer;

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody CreateOrderRequest request) {
        log.info(":: Received request to create order");

        String orderId = UUID.randomUUID().toString(); // 주문 ID 직접 생성
        OrderStartEvent event = new OrderStartEvent(
                orderId,
                request.getUserId(),
                request.getItems().stream()
                        .map(i -> new ProductOrderItem(i.getProductId(), i.getQuantity()))
                        .toList()
        );
        /*
            {
              "userId": 1234,
              "items": [
                { "productId": 101, "quantity": 2 },
                { "productId": 202, "quantity": 1 }
              ]
            }
         */
        log.info(":: sending create order event to kafka");
        orderProducer.send("order.start", event); // 순서를 위해 orderId key 사용

        return ResponseEntity.ok().build();
    }
}
