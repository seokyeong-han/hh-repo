package com.example.ecommerce.redisCache;

import com.example.ecommerce.api.coupon.dto.CouponResponse;
import com.example.ecommerce.domain.coupon.model.Coupon;
import com.example.ecommerce.domain.coupon.repository.CouponRepository;
import com.example.ecommerce.global.util.RedisCacheUtil;
import com.example.ecommerce.integration.CouponIssueIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
public class couponRedisCacheTest {
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
    private CouponRepository couponRepository;
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @BeforeEach
    void setup() {
        for (long i = 1; i <= 5000; i++) { //쿠폰 생성
            Coupon coupon = new Coupon(null,"COUPON-" + i, 1000,10,0, LocalDateTime.now(),LocalDateTime.now()); // 생성자에 맞게 수정
            couponRepository.save(coupon);

            CouponResponse response = CouponResponse.from(coupon);
            redisCacheUtil.set("cache:coupon:" + coupon.getId(), response);

        }
    }


    @Test
    void coupon_조회_성능_비교() {
        Long couponId = 1L;
        String redisKey = "cache:coupon:" + couponId;

        // 1. DB 조회 성능
        long startDb = System.nanoTime();
        couponRepository.findById(couponId).orElseThrow();
        long endDb = System.nanoTime();
        System.out.println("⛏ DB 조회 시간(ns): " + (endDb - startDb));

        // 2. Redis 조회 성능
        long startRedis = System.nanoTime();
        CouponResponse redisResult = redisCacheUtil.get(redisKey, CouponResponse.class);
        long endRedis = System.nanoTime();
        System.out.println("⚡ Redis 조회 시간(ns): " + (endRedis - startRedis));

        assertNotNull(redisResult);
        assertEquals(couponId, redisResult.id());
    }

}
