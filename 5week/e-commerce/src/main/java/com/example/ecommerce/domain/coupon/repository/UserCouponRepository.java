package com.example.ecommerce.domain.coupon.repository;

import com.example.ecommerce.domain.coupon.model.UserCoupon;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);
    List<UserCoupon> findAll();
    UserCoupon save(UserCoupon userCoupon);

}
