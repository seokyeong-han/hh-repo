package com.example.ecommerce_3week.infrastructure.order;

import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.order.OrderRepository;
import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaOrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository jpaRepository;

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id)
                .map(Order::toDomain);
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity saved = jpaRepository.save(toEntity(order));
        return Order.toDomain(saved);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
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
