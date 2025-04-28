package com.example.ecommerce.domain.coupon.repository;

import com.example.ecommerce.domain.coupon.model.Coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    List<Coupon> findAll();
    Optional<Coupon> findById(Long id);
    Coupon save(Coupon coupon);

    //비관적 락 조회
    Optional<Coupon> findWithLockById(Long id);
}
