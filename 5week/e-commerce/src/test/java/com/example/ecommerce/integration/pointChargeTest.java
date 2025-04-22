package com.example.ecommerce.integration;

import com.example.ecommerce.api.point.dto.PointCommand;
import com.example.ecommerce.common.enums.PointTransactionType;
import com.example.ecommerce.domain.point.model.PointHistory;
import com.example.ecommerce.domain.point.service.PointService;
import com.example.ecommerce.domain.user.model.User;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.ecommerce.domain.user.repository.UserRepository;
import com.example.ecommerce.domain.point.repository.PointHistoryRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class pointChargeTest {
    private static final Logger log = LoggerFactory.getLogger(pointChargeTest.class);
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
    private PointService pointService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @BeforeEach
    void setup() {

    }

    @Test
    @DisplayName("동시에 포인트 충전 시 낙관적 락으로 최종 잔액 일치")
    void chargePoint_concurrent() throws InterruptedException {
        // given
        User user = new User(null, "concurrent-user", 0L, null); //테스트용 유저
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        int threadCount = 10; //10개의 충전 요청을 동시에 날린다
        ExecutorService executor = Executors.newFixedThreadPool(threadCount); //멀티스레드 환경, 동시 10개 작업 스레드풀
        CountDownLatch latch = new CountDownLatch(threadCount);
        // 모드 스레드가 끝날 때까지 기다리게 해주는 락 도구
        // latch가 0이 될때까지 await()으로 테스트가 대기

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

        //long historyCount = pointHistoryRepository.findByUserId(userId).size();
        //assertThat(historyCount).isEqualTo(threadCount);
    }

    @Test
    @DisplayName("포인트 충전 통합 테스트")
    void chargePoint_integration() throws Exception {
        User user = new User(null, "testuser", 5_000L, null);
        User saveUser = userRepository.save(user);
        log.info("1. saveUser = userId :"+saveUser.getId()+", userName :"+saveUser.getUsername()+", userBalance :"+saveUser.getBalance());

        PointCommand command = new PointCommand(saveUser.getId(), 10_000L);
        saveUser.charge(command.getAmount());
        User chargeUser = userRepository.save(saveUser);
        log.info("2. chargUser = userId :"+chargeUser.getId()+", userName :"+chargeUser.getUsername()+", userBalance :"+chargeUser.getBalance());

        User updatedUser = userRepository.findById(chargeUser.getId()).orElseThrow();
        assertThat(updatedUser.getBalance()).isEqualTo(15_000L);

        PointHistory pointHistory = PointHistory.charge(chargeUser.getId(), command.getAmount(), chargeUser.getBalance());
        PointHistory chargeHis = pointHistoryRepository.save(pointHistory);

        log.info("3. chargeHis = chargeHisId :"+chargeHis.getId()+", chargeHisAt :"+chargeHis.getCreatedAt());
        assertThat(chargeHis.getUserId()).isEqualTo(chargeUser.getId());
        assertThat(chargeHis.getAmount()).isEqualTo(command.getAmount());
        assertThat(chargeHis.getBalance()).isEqualTo(chargeUser.getBalance());
        assertThat(chargeHis.getType()).isEqualTo(PointTransactionType.CHARGE);

    }




}
