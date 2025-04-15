package com.example.ecommerce_3week.infrastructure.ponthistory;

import com.example.ecommerce_3week.common.enums.PointTransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "point_history")
@RequiredArgsConstructor
public class PointHistoryJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private PointTransactionType type;

    private LocalDateTime createdAt;

    public PointHistoryJpaEntity(Long userId, Long amount, PointTransactionType type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }
}
