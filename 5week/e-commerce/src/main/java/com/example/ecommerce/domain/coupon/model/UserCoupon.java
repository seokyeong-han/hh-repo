package com.example.ecommerce.domain.coupon.model;

import java.time.LocalDateTime;

public class UserCoupon {
    private Long id;
    private Long userId;
    private Long couponId;
    private boolean used;
    private LocalDateTime assignedAt;

    public UserCoupon(Long id, Long userId, Long couponId, boolean used, LocalDateTime assignedAt) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.used = used;
        this.assignedAt = assignedAt;
    }

    // Getter
    public Long getId() {
        return id;
    }
    public Long getUserId() {
        return userId;
    }
    public Long getCouponId() {
        return couponId;
    }
    public boolean isUsed() {
        return used;
    }
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
}
