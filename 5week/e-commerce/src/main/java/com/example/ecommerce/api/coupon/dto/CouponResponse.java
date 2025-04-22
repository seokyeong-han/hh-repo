package com.example.ecommerce.api.coupon.dto;

import com.example.ecommerce.domain.coupon.model.Coupon;

import java.time.LocalDateTime;

public record CouponResponse(
        Long id,
        String name,
        int discountAmount,
        int totalCount,
        int issuedCount,
        int remaining,
        LocalDateTime createdAt
) {
    public static CouponResponse from(Coupon c) {
        return new CouponResponse(
                c.getId(), c.getName(), c.getDiscountAmount(),
                c.getTotalCount(), c.getIssuedCount(),
                c.getRemainingQuantity(), c.getCreatedAt()
        );
    }
}
