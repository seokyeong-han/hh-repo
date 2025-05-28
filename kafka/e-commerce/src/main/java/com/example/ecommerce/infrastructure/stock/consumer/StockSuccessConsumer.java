package com.example.ecommerce.infrastructure.stock.consumer;

import com.example.ecommerce.domain.event.StockSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockSuccessConsumer {
    private static final Logger log = LoggerFactory.getLogger(StockSuccessConsumer.class);

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

        }catch (Exception e){

        }



    }
}
