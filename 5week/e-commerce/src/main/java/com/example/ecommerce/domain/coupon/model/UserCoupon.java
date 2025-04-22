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

    public void tryUse() {
        if (used) {
            throw new IllegalStateException("이미 사용한 쿠폰입니다.");
        }
        this.used = true;
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
