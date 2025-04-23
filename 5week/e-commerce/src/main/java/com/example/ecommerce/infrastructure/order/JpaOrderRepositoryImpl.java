package com.example.ecommerce.infrastructure.order;

import com.example.ecommerce.domain.order.entity.OrderJpaEntity;
import com.example.ecommerce.domain.order.model.Order;
import com.example.ecommerce.domain.order.repository.OrderRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaOrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository jpaRepository;

    public JpaOrderRepositoryImpl(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity saved = jpaRepository.save(OrderJpaEntity.fromDomain(order));
        return saved.toDomain();
    }
}
