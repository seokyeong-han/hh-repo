package com.example.ecommerce.redisCache;

import com.example.ecommerce.domain.product.service.PopularProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class PopularProductServiceTest {
    private static final Logger log = LoggerFactory.getLogger(couponRedisCacheTest.class);

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
    PopularProductService popularProductService;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private String todayKey() {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return "popular:daily:" + today;
    }

    @BeforeEach
    void clearRedis() {
        // 테스트 간 독립성을 위해 항상 클리어
        redisTemplate.getConnectionFactory()
                .getConnection()
                .flushAll();
    }

    @Test
    @DisplayName("increase + list 호출 흐름이 실제 Redis 에서 작동한다")
    void increaseThenListIntegration() {
        long pid1 = 101, pid2 = 202, pid3 = 303;

        // 증감 호출
        popularProductService.increaseViewCount(pid1);
        popularProductService.increaseViewCount(pid2);
        popularProductService.increaseViewCount(pid1);
        popularProductService.increaseViewCount(pid3);
        popularProductService.increaseViewCount(pid3);
        popularProductService.increaseViewCount(pid3);

        // 리스트 조회
        List<Long> top2 = popularProductService.getTodayTopPopularProductIds(2);

        log.info("오늘의 TOP-2 인기 상품 ID 리스트: {}", top2);

        // 검증
        assertThat(top2).containsExactly(pid3, pid1);

        // TTL 검증
        Long ttl = redisTemplate.getExpire(todayKey());
        log.info("키 '{}' 의 남은 TTL(초): {}", todayKey(), ttl);
        assertThat(ttl)
                .isPositive()
                .isLessThanOrEqualTo(Duration.ofDays(2).getSeconds());
    }


}
