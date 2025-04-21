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

    @Enumerated(EnumType.STRING)
    private PointTransactionType type;

    private LocalDateTime createdAt;

    // JPA 엔티티 생성자
    public PointHistoryJpaEntity(Long userId, Long amount, PointTransactionType type, LocalDateTime createdAt) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    // 도메인 객체인 PointHistory를 JPA 엔티티로 변환하는 메서드
    public static PointHistoryJpaEntity fromDomain(PointHistory domain) {
        return new PointHistoryJpaEntity(
                domain.getUserId(),
                domain.getAmount(),
                domain.getType(),
                domain.getCreatedAt()
        );
    }
}
