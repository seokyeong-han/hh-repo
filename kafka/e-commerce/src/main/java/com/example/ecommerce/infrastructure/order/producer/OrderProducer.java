package com.example.ecommerce.infrastructure.order.producer;

import com.example.ecommerce.domain.event.OrderStartEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderProducer {
    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

    private final KafkaTemplate<String, OrderStartEvent> orderKafkaTemplate;

    public void send(String topic, OrderStartEvent event) {
        log.info(":: OrderProducer sending event to kafka");
        orderKafkaTemplate.send(topic, event.orderId(), event);
    }

}
