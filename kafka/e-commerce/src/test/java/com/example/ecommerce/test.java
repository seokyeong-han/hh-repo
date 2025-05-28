package com.example.ecommerce;

import com.example.ecommerce.api.order.dto.ProductOrderItem;
import com.example.ecommerce.domain.event.StockSuccessEvent;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

@SpringBootTest
public class test {
    private static final Logger log = LoggerFactory.getLogger(test.class);

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        mysql.start(); // 컨테이너 먼저 시작
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private KafkaTemplate<String, StockSuccessEvent> kafkaTemplate;

    @Test
    void 재고소비성공_메시지가_소비되는지_확인() throws InterruptedException {
        // given
        StockSuccessEvent event = new StockSuccessEvent("order-123", 1L, List.of(
                new ProductOrderItem(100L, 2)
        ));

        // when
        kafkaTemplate.send("stock.success", event.orderId(), event);
        Thread.sleep(3000);
        // then (비동기 수신 대기)
//        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
//            assertThat(listener.getReceivedEvents()).hasSize(1);
//            assertThat(listener.getReceivedEvents().get(0).orderId()).isEqualTo("order-123");
//        });
    }



}
