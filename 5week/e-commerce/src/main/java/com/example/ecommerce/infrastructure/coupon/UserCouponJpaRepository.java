package com.example.ecommerce.infrastructure.coupon;

import com.example.ecommerce.domain.coupon.entity.UserCouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCouponJpaRepository extends JpaRepository<UserCouponJpaEntity,Long> {
    Optional<UserCouponJpaEntity> findByUserIdAndCouponId(Long userId, Long couponId);
}
