package com.example.ecommerce.domain.coupon.model;

import com.example.ecommerce.domain.coupon.entity.CouponJpaEntity;

import java.time.LocalDateTime;

public class Coupon {
    private Long id;     //쿠폰 id
    private String name; //쿠폰 명
    private int discountAmount; //할인 금액
    private int totalCount;     //쿠폰  수량
    private int issuedCount;    //발급된 수량
    private LocalDateTime createdAt; // 쿠폰 생성일
    private LocalDateTime updatedAt; // 쿠폰 수정일


    // 생성자 포함
    public Coupon(Long id, String name, int discountAmount, int totalCount, int issuedCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.discountAmount = discountAmount;
        this.totalCount = totalCount;
        this.issuedCount = issuedCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    // 남은 쿠폰 수
    public int getRemainingQuantity() {
        return totalCount - issuedCount;
    }

    // 발급 가능한지 체크
    public boolean hasRemainingQuantity() {
        return getRemainingQuantity() > 0;
    }

    // 쿠폰 발급
    public void assignToUser() {
        if (!hasRemainingQuantity()) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }
        issuedCount++; // 도메인 객체 자체에서 상태 변화
    }

    // JPA 엔티티 → 도메인 객체
    public static Coupon toDomain(CouponJpaEntity entity) {
        return new Coupon(
                entity.getId(),
                entity.getName(),
                entity.getDiscountAmount(),
                entity.getTotalCount(),
                entity.getIssuedCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // Getter 추가
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getDiscountAmount() {
        return discountAmount;
    }
    public int getTotalCount() {
        return totalCount;
    }
    public int getIssuedCount() {
        return issuedCount;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
