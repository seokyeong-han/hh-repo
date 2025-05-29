package com.example.ecommerce.infrastructure.paymant.consumer;

import com.example.ecommerce.domain.event.PaymentRequestedEvent;
import com.example.ecommerce.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequestConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentRequestConsumer.class);

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "payment.request",
            groupId = "payment-service",
            containerFactory = "paymentRequestKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, PaymentRequestedEvent> record) {
        PaymentRequestedEvent event = record.value();
        String orderId = record.key(); // 또는 event.getOrderId() 사용 가능

        log.info("✅ [PaymentService] Received PaymentRequestedEvent (orderId={}): {}", orderId, event);
        try {
            paymentService.paymentProcessor(event.userId(), event.totalAmount());
            log.info("결제 성공");
        }catch (Exception e){
            log.info("결재 실패");
        }
    }
}
