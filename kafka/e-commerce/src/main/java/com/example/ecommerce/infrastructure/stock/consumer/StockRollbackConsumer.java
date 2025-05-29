package com.example.ecommerce.infrastructure.stock.consumer;

import com.example.ecommerce.domain.event.StockRollbackEvent;
import com.example.ecommerce.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockRollbackConsumer {
    private static final Logger log = LoggerFactory.getLogger(StockSuccessConsumer.class);

    private final StockService stockService;

    @KafkaListener(
            topics = "stock.rollback",
            groupId = "stock-service",
            containerFactory = "stockRollbackKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, StockRollbackEvent> record) {
        StockRollbackEvent event = record.value();
        String orderId = record.key();

        log.warn("⛔ [StockService] Received StockRollbackEvent (orderId={}): {}", orderId, event);

        try {
            stockService.rollbackStock(event.items());
            log.info("✅ 재고 롤백 완료 (orderId={})", orderId);
        }catch (Exception e) {
            log.error(" 재고 롤백 실패 (orderId={}): {}", orderId, e.getMessage(), e);
        }

    }
}
