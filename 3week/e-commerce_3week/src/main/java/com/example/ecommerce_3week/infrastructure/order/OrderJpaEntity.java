package com.example.ecommerce_3week.infrastructure.order;

import com.example.ecommerce_3week.domain.order.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // 실제 DB 테이블명
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long totalPrice;
    private LocalDateTime createdAt;

    public static OrderJpaEntity from(Order order) {
        return new OrderJpaEntity(
                order.getId(), // 저장 시 null이어도 IDENTITY 전략이면 DB가 생성해줌
                order.getUserId(),
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }

    public Order toDomain() {
        return new Order(userId, null); // 아이템은 생략 or 별도로 로딩해야 함
    }
}
