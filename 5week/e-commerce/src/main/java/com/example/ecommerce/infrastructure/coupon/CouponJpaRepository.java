package com.example.ecommerce.infrastructure.coupon;

import com.example.ecommerce.domain.coupon.entity.CouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity,Long> {


}
