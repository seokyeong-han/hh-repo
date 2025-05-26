package com.example.ecommerce.infrastructure.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MyKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public MyKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
        System.out.println("📤 Sent: " + message);
    }
}
