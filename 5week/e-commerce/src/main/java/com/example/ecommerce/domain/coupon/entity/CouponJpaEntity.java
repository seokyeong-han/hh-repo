package com.example.ecommerce.domain.coupon.entity;

import com.example.ecommerce.domain.coupon.model.Coupon;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "coupon")
public class CouponJpaEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int discountAmount;
    private int totalCount;
    private int issuedCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public CouponJpaEntity() {} //기본 생성자

    public CouponJpaEntity(Long id, String name, int discountAmount, int totalCount, int issuedCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.discountAmount = discountAmount;
        this.totalCount = totalCount;
        this.issuedCount = issuedCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // JPA → 도메인
    public Coupon toDomain() {
        return new Coupon(
                this.id,
                this.name,
                this.discountAmount,
                this.totalCount,
                this.issuedCount,
                this.createdAt,
                this.updatedAt
        );
    }

    public static CouponJpaEntity fromDomain(Coupon coupon) {
        return new CouponJpaEntity(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountAmount(),
                coupon.getTotalCount(),
                coupon.getIssuedCount(),
                coupon.getCreatedAt(),
                coupon.getUpdatedAt()
        );
    }

}
