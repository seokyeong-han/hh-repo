package com.example.ecommerce.infrastructure.paymant.consumer;

import com.example.ecommerce.domain.event.OrderCancelEvent;
import com.example.ecommerce.domain.event.PaymentRequestedEvent;
import com.example.ecommerce.domain.event.PaymentSuccessEvent;
import com.example.ecommerce.domain.event.StockRollbackEvent;
import com.example.ecommerce.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequestConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentRequestConsumer.class);

    private final PaymentService paymentService;
    //private final KafkaTemplate<String, PaymentSuccessEvent> kafkaTemplate;
    private final KafkaTemplate<String, OrderCancelEvent> kafkaTemplate2;
    private final KafkaTemplate<String, StockRollbackEvent> kafkaTemplate3;



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
            //kafkaTemplate.send("payment.success", orderId, new PaymentSuccessEvent());
        }catch (Exception e){
            log.info("결재 실패 :: 주문 취소, 재고롤백 이벤트 발행 ==>"+e.getMessage());
            //1.주문 취소 이벤트 발행
            kafkaTemplate2.send("order.cencel", orderId, new OrderCancelEvent(
                    orderId, event.resultOrderId(), event.userId()
            ));
            //2.재고 롤백 이벤트 발행
            kafkaTemplate3.send("stock.rollback", orderId, new StockRollbackEvent(
                    orderId, event.userId(), event.items()
            ));
        }
    }
}
