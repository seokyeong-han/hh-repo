package com.example.ecommerce_3week.infrastructure.order;

import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.infrastructure.orderitem.OrderItemJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 실제 DB 테이블명
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC) // 기존에 PROTECTED였다면 이걸로 변경
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long totalPrice;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemJpaEntity> items = new ArrayList<>();

    public OrderJpaEntity(Long id, Long userId, Long totalPrice, LocalDateTime createdAt) { //생성자 생성
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }


    public Order toDomain() {
        return new Order(userId, null); // 아이템은 생략 or 별도로 로딩해야 함
    }
}
