package com.example.ecommerce.infrastructure.stock.consumer;

import com.example.ecommerce.domain.event.OrderStartEvent;
import com.example.ecommerce.domain.event.StockSuccessEvent;
import com.example.ecommerce.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockConsumer {
    private static final Logger log = LoggerFactory.getLogger(StockConsumer.class);

    private final StockService stockService;
    private final KafkaTemplate<String, StockSuccessEvent> kafkaTemplate;

    @KafkaListener(
            topics = "order.start",
            groupId = "stock-service",
            containerFactory = "orderKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, OrderStartEvent> record) {
        OrderStartEvent event = record.value();
        String orderId = record.key(); // 또는 event.getOrderId() 사용 가능

        log.info("✅ [StockService] Received OrderStartEvent (orderId={}): {}", orderId, event);
        log.info("✅ Stock reduction completed for orderId={}", orderId);

        //재고 차감 service 호출
        try {
            stockService.reduceStock(record.value(), record.key());
            kafkaTemplate.send("stock.success", orderId, new StockSuccessEvent(
                    orderId, event.userId(), event.items()
            ));
        } catch (Exception e) {
            log.error("재고 차감 실패: {}", e.getMessage());
        }


    }
}
