package com.example.ecommerce.config;

import com.example.ecommerce.domain.event.OrderStartEvent;
import com.example.ecommerce.domain.event.PaymentRequestedEvent;
import com.example.ecommerce.domain.event.StockSuccessEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    // ProducerFactory 생성
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }
    // KafkaTemplate 생성
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private Map<String, Object> baseProducerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return config;
    }

    // OrderEvent
    @Bean
    public ProducerFactory<String, OrderStartEvent> orderProducerFactory() {
        Map<String, Object> config = baseProducerConfig();
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, OrderStartEvent> orderKafkaTemplate() {
        return new KafkaTemplate<>(orderProducerFactory());
    }

    // StockEvent
    @Bean
    public ProducerFactory<String, StockSuccessEvent> stockSuccessProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, StockSuccessEvent> stockSuccessKafkaTemplate() {
        return new KafkaTemplate<>(stockSuccessProducerFactory());
    }

    // PaymentEvent
    @Bean
    public ProducerFactory<String, PaymentRequestedEvent> paymentRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerConfig());
    }
    @Bean
    public KafkaTemplate<String, PaymentRequestedEvent> paymentRequestKafkaTemplate() {
        return new KafkaTemplate<>(paymentRequestProducerFactory());
    }

    /*

    // PointEvent
    @Bean
    public ProducerFactory<String, PointEvent> pointProducerFactory() {
        Map<String, Object> config = baseProducerConfig();
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, PointEvent> pointKafkaTemplate() {
        return new KafkaTemplate<>(pointProducerFactory());
    }*/


}
