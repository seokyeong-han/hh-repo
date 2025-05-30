package com.example.ecommerce.infrastructure.order.consumer;

import com.example.ecommerce.domain.event.OrderCancelEvent;
import com.example.ecommerce.domain.order.service.OrderService;
import com.example.ecommerce.infrastructure.paymant.consumer.PaymentRequestConsumer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCancelConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderCancelConsumer.class);
    private final OrderService orderService;

    @KafkaListener(
            topics = "order.cancel",
            groupId = "order-service",
            containerFactory = "orderCancelKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, OrderCancelEvent> record){
        OrderCancelEvent event = record.value();
        String orderId = record.key(); // 또는 event.orderId() 사용 가능

        log.warn(" 주문 취소 이벤트 수신 (orderId={}): {}", orderId, event);

        try {
            orderService.cancelOrder(event.resultOrderId(), event.userId());
        }catch (Exception e){
            log.error(" 주문 취소 실패 (orderId={}): {}", orderId, e.getMessage(), e);
        }
    }
}
