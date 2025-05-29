package com.example.ecommerce.infrastructure.stock.consumer;

import com.example.ecommerce.domain.event.PaymentRequestedEvent;
import com.example.ecommerce.domain.event.StockSuccessEvent;
import com.example.ecommerce.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockSuccessConsumer {
    private static final Logger log = LoggerFactory.getLogger(StockSuccessConsumer.class);

    private final OrderService orderService;
    private final KafkaTemplate<String, PaymentRequestedEvent> kafkaTemplate;

    //private final RedisTemplate<String, String> redisTemplate;

    @KafkaListener(
            topics = "stock.success",
            groupId = "order-service",
            containerFactory = "stockSuccessKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, StockSuccessEvent> record) {
        StockSuccessEvent event = record.value();
        String orderId = record.key(); // 또는 event.getOrderId() 사용 가능

        log.info("✅ [OrderService] Received StockSuccessEvent (orderId={}): {}", orderId, event);

        try {
            //주문 생성 실행
            Long totalAmount = orderService.placeOrder(event.userId(), event.items()); //total가격 리턴하면 될듯
            //결제 이벤트 발행
            kafkaTemplate.send("payment.request", orderId, new PaymentRequestedEvent(
                    orderId, event.userId(), totalAmount
            ));
            log.info(":: 주문 성공");
        }catch (Exception e){
            log.info(":: 주문 실패");
        }



    }
}
