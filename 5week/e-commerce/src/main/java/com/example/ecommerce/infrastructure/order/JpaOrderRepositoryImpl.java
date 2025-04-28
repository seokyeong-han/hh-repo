package com.example.ecommerce.infrastructure.order;

import com.example.ecommerce.domain.order.entity.OrderJpaEntity;
import com.example.ecommerce.domain.order.model.Order;
import com.example.ecommerce.domain.order.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaOrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository jpaRepository;

    public JpaOrderRepositoryImpl(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream()
                .map(OrderJpaEntity::toDomain)// JPA → 도메인
                .toList();
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity saved = jpaRepository.save(OrderJpaEntity.fromDomain(order));
        return saved.toDomain();
    }
}
