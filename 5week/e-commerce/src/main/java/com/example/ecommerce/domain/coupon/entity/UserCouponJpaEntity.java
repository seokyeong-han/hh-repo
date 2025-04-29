package com.example.ecommerce.domain.coupon.entity;

import com.example.ecommerce.domain.coupon.model.UserCoupon;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "couponId"})
        })
public class UserCouponJpaEntity {
    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private Long couponId;

    private LocalDateTime assignedAt; //쿠폰 발급일
    private boolean used; // 사용 여부

    protected UserCouponJpaEntity() {} //기본 생성자

    public UserCouponJpaEntity(Long id, Long userId, Long couponId, boolean used, LocalDateTime assignedAt) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.used = used;
        this.assignedAt = assignedAt;
    }

    // 도메인 → JPA 변환
    public static UserCouponJpaEntity fromDomain(UserCoupon userCoupon) {
        return new UserCouponJpaEntity(
                userCoupon.getId(),
                userCoupon.getUserId(),
                userCoupon.getCouponId(),
                userCoupon.isUsed(),
                userCoupon.getAssignedAt()
        );
    }

    //JPA → 도메인
    public UserCoupon toDomain() {
        return new UserCoupon(id, userId, couponId, used, assignedAt);
    }

    
}
