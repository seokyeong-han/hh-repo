package com.example.ecommerce.domain.point.entity;

import com.example.ecommerce.common.enums.PointTransactionType;
import com.example.ecommerce.domain.point.model.PointHistory;
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

    private Long balance;

    @Enumerated(EnumType.STRING)
    private PointTransactionType type;

    private LocalDateTime createdAt;

    // JPA 엔티티 생성자
    public PointHistoryJpaEntity(Long userId, Long amount, Long balance, PointTransactionType type, LocalDateTime createdAt) {
        this.userId = userId;
        this.amount = amount;
        this.balance = balance;
        this.type = type;
        this.createdAt = createdAt;
    }

    // 도메인 → JPA
    public static PointHistoryJpaEntity fromDomain(PointHistory domain) {
        return new PointHistoryJpaEntity(
                domain.getUserId(),
                domain.getAmount(),
                domain.getBalance(),
                domain.getType(),
                domain.getCreatedAt()
        );
    }

    // JPA → 도메인
    public PointHistory toDomain() {
        return new PointHistory(
                this.id,
                this.userId,
                this.amount,
                this.balance,
                this.type,
                this.createdAt
        );
    }
}
