package com.example.ecommerce.domain.coupon.service;

import com.example.ecommerce.domain.coupon.model.Coupon;
import com.example.ecommerce.domain.coupon.model.UserCoupon;
import com.example.ecommerce.domain.coupon.repository.CouponRepository;
import com.example.ecommerce.domain.coupon.repository.UserCouponRepository;
import jakarta.persistence.PessimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.annotation.Retryable;

import java.sql.SQLTransientConnectionException;
import java.sql.SQLTransientException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {
    private static final Logger log = LoggerFactory.getLogger(CouponService.class);

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public  CouponService(CouponRepository couponRepository, UserCouponRepository userCouponRepository) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
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

    @Retryable( //비관적 락을 걸어 쿠폰 조회를 막았지만 15명 시도중 다 성공하지 못해 재시도 로직을 구현
            value = {
                    PessimisticLockException.class
                    ,CannotAcquireLockException.class
                    ,SQLTransientConnectionException.class // 커넥션 풀 부족 시
                    ,SQLTransientException.class            // DB 락 타임아웃 대응
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 150)
    )
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

        coupon.assignToUser();
        couponRepository.save(coupon);

        //유저 쿠폰 발급 (used = false)
        UserCoupon userCoupon = new UserCoupon(null, userId, couponId, false, LocalDateTime.now());
        userCouponRepository.save(userCoupon);

        //히스토리 기능은 추후 개발 예정

    }



}
