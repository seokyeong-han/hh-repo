package com.example.ecommerce_3week.domain.pointhistory;

import com.example.ecommerce_3week.common.enums.PointTransactionType;

import java.time.LocalDateTime;

public class PointHistory {
    private Long id;
    private Long userId;
    private Long amount;
    private PointTransactionType type;
    private LocalDateTime createdAt;

    public PointHistory(Long userId, Long amount, PointTransactionType type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public PointHistory(Long id, Long userId, Long amount, PointTransactionType type, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

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
