package com.example.ecommerce.api.coupon.facade;

import com.example.ecommerce.api.coupon.dto.CouponResponse;
import com.example.ecommerce.domain.coupon.service.CouponService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponFacade {
    private final CouponService couponService;

    public CouponFacade(CouponService couponService) {
        this.couponService = couponService;
    }

    public List<CouponResponse> getAllCoupon() {
        return couponService.getAll().stream()
                .map(CouponResponse::from)
                .toList();
    }

    public CouponResponse getCouponById(Long id) {
        return CouponResponse.from(couponService.getById(id));
    }

    //쿠폰 발급
    public void assignCouponToUser(Long couponId, Long userId) {
        couponService.assignCouponToUser(couponId, userId);
    }
}
