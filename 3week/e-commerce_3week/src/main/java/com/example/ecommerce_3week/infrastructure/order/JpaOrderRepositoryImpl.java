package com.example.ecommerce_3week.infrastructure.order;

import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaOrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository jpaRepository;

    @Override
    public void save(Order order) {
        jpaRepository.save(toEntity(order));
    }

    // JPA Entity → 도메인 모델
    private Order toDomain(OrderJpaEntity entity) {
        return new Order(
                entity.getUserId(),
                List.of() // 아이템은 따로 조회하거나 추가 매핑 로직 구현 가능
        );
    }

    // 도메인 → JPA Entity
    private OrderJpaEntity toEntity(Order order) {
        return new OrderJpaEntity(
                order.getId(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }
}
