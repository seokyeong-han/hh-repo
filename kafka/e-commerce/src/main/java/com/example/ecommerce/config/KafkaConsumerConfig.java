package com.example.ecommerce.config;

import com.example.ecommerce.domain.event.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConsumerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 과거부터 읽기
        return config;
    }

    @Bean
    public ConsumerFactory<String, OrderStartEvent> orderConsumerFactory() {//ConsumerFactory는 메시지를 읽어오는 설정
        JsonDeserializer<OrderStartEvent> deserializer = new JsonDeserializer<>(OrderStartEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> config = new HashMap<>(baseConsumerConfig());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "stock-service"); // 여기서 서비스 설정

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderStartEvent> orderKafkaListenerContainerFactory() {
        //메시지를 받아서 @KafkaListener로 전달 해주는 역할
        ConcurrentKafkaListenerContainerFactory<String, OrderStartEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderConsumerFactory()); // 여기에 ConsumerFactory 주입!
        return factory;
    }
    //orderCancel
    @Bean
    public ConsumerFactory<String, OrderCancelEvent> orderCancelConsumerFactory() {
        JsonDeserializer<OrderCancelEvent> deserializer = new JsonDeserializer<>(OrderCancelEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        Map<String, Object> config = new HashMap<>(baseConsumerConfig());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "order-service");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCancelEvent> orderCancelKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderCancelEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCancelConsumerFactory());
        return factory;
    }
    ///////////
    //stockSuccess
    @Bean
    public ConsumerFactory<String, StockSuccessEvent> stockSuccessConsumerFactory() {
        JsonDeserializer<StockSuccessEvent> deserializer = new JsonDeserializer<>(StockSuccessEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> config = new HashMap<>(baseConsumerConfig());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "order-service");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StockSuccessEvent> stockSuccessKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StockSuccessEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stockSuccessConsumerFactory());
        return factory;
    }
    //stock rollback
    @Bean
    public ConsumerFactory<String, StockRollbackEvent> stockRollbackConsumerFactory() {
        JsonDeserializer<StockRollbackEvent> deserializer = new JsonDeserializer<>(StockRollbackEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        Map<String, Object> config = new HashMap<>(baseConsumerConfig());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "stock-service");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),deserializer);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StockRollbackEvent> stockRollbackKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StockRollbackEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stockRollbackConsumerFactory());
        return factory;
    }
    ///////////
    //payment
    @Bean
    public ConsumerFactory<String, PaymentRequestedEvent>  paymentRequestConsumerFactory() {
        JsonDeserializer<PaymentRequestedEvent> deserializer = new JsonDeserializer<>(PaymentRequestedEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        Map<String, Object> config = new HashMap<>(baseConsumerConfig());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-service");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentRequestedEvent> paymentRequestKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentRequestedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentRequestConsumerFactory());
        return factory;
    }



}
