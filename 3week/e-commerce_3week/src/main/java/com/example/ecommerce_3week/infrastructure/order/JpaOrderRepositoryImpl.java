package com.example.ecommerce_3week.infrastructure.order;

import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaOrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        OrderJpaEntity saved = jpaRepository.save(toEntity(order));
        return Order.toDomain(saved);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream()
                .map(Order::toDomain)
                .collect(Collectors.toList());
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
