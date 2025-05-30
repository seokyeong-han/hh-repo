package com.example.ecommerce.api.kafka.controller;

import com.example.ecommerce.infrastructure.kafka.producer.MyKafkaProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
public class kafkaController {
    private final MyKafkaProducer producer;

    public kafkaController(MyKafkaProducer producer) {
        this.producer = producer;
    }

    @GetMapping("/send")
    public String send(@RequestParam String message) {
        producer.send("test-topic", message);
        return "Message sent!";
    }
}
