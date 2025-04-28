package com.example.ecommerce.redis;

import com.example.ecommerce.api.point.dto.PointCommand;
import com.example.ecommerce.domain.point.repository.PointHistoryRepository;
import com.example.ecommerce.domain.point.service.PointService;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
@SuppressWarnings("resource")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class pointChargeRedisTest {
    private static final Logger log = LoggerFactory.getLogger(pointChargeRedisTest.class);

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2.4")
            .withExposedPorts(6379); // Redis 기본 포트 6379

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        mysql.start(); // 컨테이너 먼저 시작
        redis.start(); // Redis 컨테이너 시작
        //mySql
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        //redis
        registry.add("spring.data.redis.host", () -> redis.getHost());
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private PointService pointService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @BeforeEach
    void setup() {
        // 필요시 초기화
    }

    @Test
    @DisplayName("동시에 포인트 충전 시 Redisson 락 + 낙관적 락 최종 잔액 일치")
    void chargePoint_concurrent() throws InterruptedException {
        // given
        User user = new User(null, "concurrent-user", 0L, null);
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    pointService.chargePoint(new PointCommand(userId, 1000L));
                } catch (Exception e) {
                    log.error("충전 실패", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        User updatedUser = userRepository.findById(userId).orElseThrow();
        assertThat(updatedUser.getBalance()).isEqualTo(1000L * threadCount);

        // 히스토리 체크하고 싶으면 여기서
        // assertThat(pointHistoryRepository.findByUserId(userId).size()).isEqualTo(threadCount);
    }



}
