package com.example.ecommerce.infrastructure.scheduler;

import com.example.ecommerce.domain.coupon.model.UserCoupon;
import com.example.ecommerce.domain.coupon.repository.CouponRepository;
import com.example.ecommerce.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponScheduler {
    private final CouponService couponService;

    @Scheduled(fixedDelay = 5000) // 5초마다 큐 처리
    public void processCouponQueue() {
        couponService.processQueue();
    }
}
