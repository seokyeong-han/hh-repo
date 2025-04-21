package com.example.ecommerce.domain.point.model;

import com.example.ecommerce.common.enums.PointTransactionType;
import java.time.LocalDateTime;

public class PointHistory {
    private Long id;
    private Long userId;
    private Long amount;
    private PointTransactionType type;
    private LocalDateTime createdAt;

    // 기본 생성자 추가
    public PointHistory() {
        // 기본 생성자에서 초기화할 값이 있다면 여기에 설정할 수 있음
        this.createdAt = LocalDateTime.now();  // 예시로 생성 시간 기본값을 설정
    }

    // userId, amount, type을 받는 생성자 추가
    public PointHistory(Long userId, Long amount, PointTransactionType type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    // 정적 팩토리 메서드 charge
    public static PointHistory charge(Long userId, Long amount) {
        // PointTransactionType을 충전으로 설정
        return new PointHistory(userId, amount, PointTransactionType.CHARGE);
    }


    //getter
    public Long getUserId() {
        return userId;
    }
    public Long getAmount() {
        return amount;
    }
    public PointTransactionType getType() {
        return type;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public Long getId() {
        return id;
    }
}
