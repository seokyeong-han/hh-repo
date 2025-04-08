package com.example.ecommerce_3week.domain.history;

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
}
