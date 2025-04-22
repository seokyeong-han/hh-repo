package com.example.ecommerce.domain.coupon.service;

import com.example.ecommerce.domain.coupon.model.Coupon;
import com.example.ecommerce.domain.coupon.model.UserCoupon;
import com.example.ecommerce.domain.coupon.repository.CouponRepository;
import com.example.ecommerce.domain.coupon.repository.UserCouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {
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
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
    }

    @Transactional
    public void assignCouponToUser(Long couponId, Long userId) {
        // 유저가 이 쿠폰을 발급받은 적 있는지 확인
        Optional<UserCoupon> optional = userCouponRepository.findByUserIdAndCouponId(userId, couponId);

        if (optional.isPresent()) { //Optional 안에 값이 있으면 true, 없으면 false를 반환
            UserCoupon userCoupon = optional.get();

            if (userCoupon.isUsed()) {
                throw new IllegalStateException("이미 사용한 쿠폰입니다. 재발급할 수 없습니다.");
            }

            throw new IllegalStateException("이미 쿠폰을 발급받았습니다.");
        }

        // 2. 쿠폰 재고 확인 및 발급 처리
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        coupon.assignToUser();
        couponRepository.save(coupon);

        // 3. 유저 쿠폰 발급 (used = false)
        UserCoupon userCoupon = new UserCoupon(null, userId, couponId, false, LocalDateTime.now());
        userCouponRepository.save(userCoupon);

    }



}
