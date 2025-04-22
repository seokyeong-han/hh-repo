package com.example.ecommerce.domain.coupon.repository;

import com.example.ecommerce.domain.coupon.model.UserCoupon;

import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);
    void save(UserCoupon userCoupon);

}
