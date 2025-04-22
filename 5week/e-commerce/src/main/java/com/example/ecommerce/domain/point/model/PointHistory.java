package com.example.ecommerce.domain.point.model;

import com.example.ecommerce.common.enums.PointTransactionType;
import java.time.LocalDateTime;

public class PointHistory {
    private Long id;
    private Long userId;
    private Long amount; //충전 금액
    private Long balance; //총 금액
    private PointTransactionType type;
    private LocalDateTime createdAt;

    //기본 생성자
    public PointHistory(Long userId, Long amount, Long balance, PointTransactionType type, LocalDateTime createdAt) {
        this.userId = userId;
        this.amount = amount;
        this.balance = balance;
        this.type = type;
        this.createdAt = createdAt;
    }

    public PointHistory(Long id, Long userId, Long amount, Long balance, PointTransactionType type, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.balance = balance;
        this.type = type;
        this.createdAt = createdAt;
    }

    public PointHistory(Long userId, Long amount, Long balance, PointTransactionType type) {
        this.userId = userId;
        this.amount = amount;
        this.balance = balance;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }



    // 정적 팩토리 메서드 charge
    public static PointHistory charge(Long userId, Long amount, Long balance) {
        // PointTransactionType을 충전으로 설정
        return new PointHistory(userId, amount, balance, PointTransactionType.CHARGE);
    }


    //getter
    public Long getUserId() {
        return userId;
    }
    public Long getAmount() {
        return amount;
    }
    public Long getBalance() { return balance;}
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
