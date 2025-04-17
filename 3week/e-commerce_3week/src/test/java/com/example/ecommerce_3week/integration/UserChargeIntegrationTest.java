package com.example.ecommerce_3week.integration;

import com.example.ecommerce_3week.common.enums.PointTransactionType;
import com.example.ecommerce_3week.domain.pointhistory.PointHistory;
import com.example.ecommerce_3week.domain.pointhistory.PointHistoryRepository;
import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.domain.user.UserRepository;
import com.example.ecommerce_3week.dto.user.UserChargeRequest;
import com.example.ecommerce_3week.facade.UserFacade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserChargeIntegrationTest {
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
    private UserRepository userRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private UserFacade userFacade; // ← 파사드 주입

    @Test
    public void 유저_충전_정상작동() {
        // given
        User user = new User("testuser", 100L);
        User savedUser = userRepository.save(user);
        Long chargeAmount = 50L;

        UserChargeRequest request = new UserChargeRequest(savedUser.getId(), chargeAmount);

        // when
        userFacade.chargeUser(request); // ← 파사드 호출

        // then
        User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(updatedUser.getBalance()).isEqualTo(150L);

        // 충전 히스토리 존재 여부 확인
        List<PointHistory> histories = pointHistoryRepository.findByUserId(savedUser.getId());
        assertThat(histories).isNotEmpty();

        PointHistory history = histories.get(0);
        assertThat(history.getUserId()).isEqualTo(savedUser.getId());
        assertThat(history.getAmount()).isEqualTo(150L); // 또는 chargeAmount
        assertThat(history.getType()).isEqualTo(PointTransactionType.CHARGE);
    }
}
