package com.example.ecommerce.integration;

import com.example.ecommerce.domain.coupon.model.Coupon;
import com.example.ecommerce.domain.coupon.model.UserCoupon;
import com.example.ecommerce.domain.coupon.repository.CouponRepository;
import com.example.ecommerce.domain.coupon.repository.UserCouponRepository;
import com.example.ecommerce.domain.coupon.service.CouponService;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CouponIssueIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(CouponIssueIntegrationTest.class);
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

    private User user;
    private Coupon coupon;
    @BeforeEach
    void setup() {
        user = new User(null, "testuser", 5_000L, null);
        user = userRepository.save(user);

        coupon = couponRepository.save(new Coupon(null, "테스트 쿠폰", 1000, 10, 0, LocalDateTime.now(), LocalDateTime.now()));
        coupon = couponRepository.save(coupon);
    }


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;
    @Autowired
    private CouponService couponService;

    @Test
    @DisplayName("선착순 쿠폰 발급 - 동시에 여러 유저가 요청해도 최대 수량까지만 발급된다.")
    void assignCoupon_concurrent_withLock() throws Exception {
        // ✅ 테스트 전용 쿠폰 생성 (issuedCount = 0, totalCount = 10)
        Coupon testCoupon = couponRepository.save(new Coupon(
                null, "동시성 쿠폰", 1000, 10, 0, LocalDateTime.now(), LocalDateTime.now()
        ));

        int threadCount = 15;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Long> successList = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            int userIndex = i;
            executorService.submit(() -> {
                try {
                    // 유저 생성 및 저장
                    User user = new User(null, "user" + userIndex, 0L, null);
                    user = userRepository.save(user);

                    // 쿠폰 발급 시도
                    try {
                        couponService.assignCouponToUser(testCoupon.getId(), user.getId());
                        successList.add(user.getId()); // 발급 성공한 유저만 기록
                    } catch (Exception e) {
                        // 발급 실패는 무시
                    }

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // ✅ 발급 성공 유저 수는 최대 수량(10)을 넘지 않아야 한다.
        assertThat(successList.size()).isEqualTo(10);

        // ✅ 실제 발급된 UserCoupon 수가 5인지 확인
        List<UserCoupon> allUserCoupons = userCouponRepository.findAll();
        assertThat(allUserCoupons.size()).isEqualTo(10);

        // ✅ 쿠폰 발급 수량 증가 확인
        Coupon issuedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        assertThat(issuedCoupon.getIssuedCount()).isEqualTo(10);

        log.info("✅ 발급 성공한 유저 수: {}", successList.size());
        log.info("❌ 실패한 유저 수: {}", threadCount - successList.size());
        for (Long userId : successList) {
            userRepository.findById(userId).ifPresent(user -> {
                log.info("✅ 발급 성공 유저 - ID: {}, Name: {}, Balance: {}", user.getId(), user.getUsername(), user.getBalance());
            });
        }

    }

    @Test
    @DisplayName("쿠폰 중복 발급 시 IllegalStateException 발생")
    void assignCoupon_duplicateIssue_throwsException() {
        // given: 유저와 쿠폰, 이미 발급받은 이력 저장
        UserCoupon issuedCoupon = new UserCoupon(null, user.getId(), coupon.getId(), false, LocalDateTime.now());
        userCouponRepository.save(issuedCoupon);

        // when & then: 예외 발생 검증
        assertThrows(IllegalStateException.class, () -> {
            couponService.assignCouponToUser(coupon.getId(), user.getId());
            //userCoupon에 이미 발급 되어있어 예외 발생
        });
    }


    @Test
    @DisplayName("쿠폰 최초 발급 성공")
    void assignCoupon_firstTime_success() { //Optional은 true, false를 return
        log.info("test user ==> ID:"+user.getId()+", NAME: "+user.getUsername());
        log.info("test coupon ==> ID:"+coupon.getId()+", NAME: "+coupon.getName());

        userCouponRepository.findByUserIdAndCouponId(user.getId(), coupon.getId());
        Coupon useCoupon = couponRepository.findById(coupon.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        useCoupon.assignToUser();
        Coupon updateCoupon = couponRepository.save(useCoupon);

        log.info("쿠폰 갯수 차감 => ID :"+updateCoupon.getId()+", 쿠폰 발급 갯수 : "+updateCoupon.getTotalCount()
                +", 쿠폰 사용 횟수 : "+updateCoupon.getIssuedCount()+", 남은 쿠폰 갯수"+updateCoupon.getRemainingQuantity());

        assertThat(updateCoupon.getTotalCount()).isEqualTo(10);   // 생성된 쿠폰 갯수
        assertThat(updateCoupon.getIssuedCount()).isEqualTo(1);  // 발급된 쿠폰 갯수
        assertThat(updateCoupon.getRemainingQuantity()).isEqualTo(9); // 남은 쿠폰 갯수

        UserCoupon userCoupon = new UserCoupon(null, user.getId(), useCoupon.getId(), false, LocalDateTime.now());
        UserCoupon savedUserCoupon = userCouponRepository.save(userCoupon);

        log.info("사용자 발급 쿠폰 => ID :"+savedUserCoupon.getCouponId()+", 발급 ID : "+savedUserCoupon.getId());

    }

}
