package com.example.ecommerce.redisCache;

import com.example.ecommerce.domain.coupon.model.UserCoupon;
import com.example.ecommerce.domain.coupon.repository.UserCouponRepository;
import com.example.ecommerce.domain.coupon.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class CouponZSetQueueTest {
    private static final Logger log = LoggerFactory.getLogger(CouponZSetQueueTest.class);

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
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        registry.add("spring.data.redis.host", () -> redis.getHost());
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        // 쿠폰 재고 세팅
        redisTemplate.opsForValue().set("coupon:stock:1001", "3");
    }

    @Test
    void testConcurrentZSetCouponIssue() {
        // 5명 유저가 동시에 요청
        for (int i = 1; i <= 5; i++) {
            couponService.requestCoupon((long) i, 1001L); // ZADD
        }

        // 발급 처리
        couponService.processQueue(); // ZREVRANGE + 처리

        // 결과 검증
        List<UserCoupon> issued = userCouponRepository.findAll();
        issued.forEach(c -> System.out.println("✅ 발급됨: " + c.getUserId()));

        assertThat(issued).hasSize(3); // 재고 3개까지만 발급

        //쿠폰이 사용되면 쿠폰 히스토리로 관리하고
        // 재고가 없거나 쿠폰 유효기간이 지나면 삭제
    }

}
