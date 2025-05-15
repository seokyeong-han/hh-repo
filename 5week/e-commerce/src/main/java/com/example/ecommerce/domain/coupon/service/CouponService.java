package com.example.ecommerce.domain.coupon.service;

import com.example.ecommerce.domain.coupon.model.Coupon;
import com.example.ecommerce.domain.coupon.model.UserCoupon;
import com.example.ecommerce.domain.coupon.repository.CouponRepository;
import com.example.ecommerce.domain.coupon.repository.UserCouponRepository;
import com.example.ecommerce.global.annotation.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class CouponService {
    private static final Logger log = LoggerFactory.getLogger(CouponService.class);

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public  CouponService(CouponRepository couponRepository, UserCouponRepository userCouponRepository, RedisTemplate<String, String> redisTemplate) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional(readOnly = true)
    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Coupon getById(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));
    }


    /*@Retryable( //비관적 락을 걸어 쿠폰 조회를 막았지만 15명 시도중 다 성공하지 못해 재시도 로직을 구현
            value = {
                    PessimisticLockException.class
                    ,CannotAcquireLockException.class
                    ,SQLTransientConnectionException.class // 커넥션 풀 부족 시
                    ,SQLTransientException.class            // DB 락 타임아웃 대응
            },
            backoff = @Backoff(delay = 50)
    )*/
    @RedisLock(key = "'lock:coupon:' + #couponId"
            , waitTime = 5
            , leaseTime = 1
            , timeUnit = TimeUnit.SECONDS)
    @Transactional
    public void assignCouponToUser(Long couponId, Long userId) {
        log.info("🟡 시도 - userId={}, couponId={}", userId, couponId);

        // 유저가 이 쿠폰을 발급받은 적 있는지 확인
        userCouponRepository.findByUserIdAndCouponId(userId, couponId)
                .ifPresent(userCoupon -> {throw new IllegalStateException("이미 이 쿠폰을 발급받은 유저입니다.");
                });

        //쿠폰 재고 확인 및 발급 처리
        //Coupon coupon = couponRepository.findById(couponId)
        Coupon coupon = couponRepository.findWithLockById(couponId) //비관적 락으로 쿠폰 조회
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        //유저 쿠폰 발급 (used = false)
        UserCoupon userCoupon = new UserCoupon(null, userId, couponId, false, LocalDateTime.now());
        userCouponRepository.save(userCoupon);

        coupon.assignToUser();  // 도메인에서 재고 체크 및 증가
        couponRepository.save(coupon);

        //히스토리 기능은 추후 개발 예정
    }

    //redis를 사용한 쿠폰 발급
    //대기 사용
    //중복 쿠폰 발급의 경우 아직 구현 x
    //단일 쿠폰만 사용가능
    public void requestCoupon(Long userId, Long couponId) {
        String queueKey = "coupon:queue";
        String value = userId + ":" + couponId;
        double score = System.currentTimeMillis(); // milliseconds timestamp

        redisTemplate.opsForZSet().add(queueKey, value, score);
    }

    public void processQueue() {
        String queueKey = "coupon:queue";

        // 최근 요청된 순서대로 최대 100개 처리
        Set<String> entries = redisTemplate.opsForZSet().range(queueKey, 0, 99);

        if (entries == null || entries.isEmpty()) return; //발급 대기 인원 없음

        // ✅ 들어온 큐 요청 로그 출력
        log.info("📝 발급 요청 큐 조회 (최대 100건):");
        entries.forEach(entry -> {
            String[] parts = entry.split(":");
            Long userId = Long.valueOf(parts[0]);
            Long couponId = Long.valueOf(parts[1]);

            // score도 함께 출력하고 싶다면 ZSet score 조회
            Double score = redisTemplate.opsForZSet().score(queueKey, entry);

            log.info(" - 요청 userId={}, couponId={}, timestamp={}", userId, couponId, score != null ? score.longValue() : "null");
        });


        for (String entry : entries) {
            String[] parts = entry.split(":");
            Long userId = Long.valueOf(parts[0]);
            Long couponId = Long.valueOf(parts[1]);

            // 중복 체크
            String issuedKey = "coupon:issued:" + couponId;
            if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(issuedKey, userId.toString()))) {
                continue;
            }

            // 재고 차감
            String stockKey = "coupon:stock:" + couponId;
            Long stock = redisTemplate.opsForValue().decrement(stockKey);
            if (stock == null || stock < 0) {
                redisTemplate.opsForValue().increment(stockKey); // 롤백
                continue;
            }

            // 중복 방지 등록 + 발급 처리
            redisTemplate.opsForSet().add(issuedKey, userId.toString());
            userCouponRepository.save(new UserCoupon(null, userId, couponId, false, LocalDateTime.now()));

            // ZSet에서 삭제
            redisTemplate.opsForZSet().remove(queueKey, entry); // 발급 완료된 userId:couponId 항목 Redis ZSet(대기열)에서 제거
        }
    }


}
