package com.example.ecommerce.infrastructure.order.jpaRepository.order;

import com.example.ecommerce.domain.order.entity.order.OrderJpaEntity;
import com.example.ecommerce.domain.order.model.order.Order;
import com.example.ecommerce.domain.order.repository.order.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaOrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository jpaRepository;

    public JpaOrderRepositoryImpl(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id)
                .map(Order::toDomain);
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity saved = jpaRepository.save(OrderJpaEntity.fromDomain(order));
        return Order.toDomain(saved);
    }

    @Override
    public void deleteByOrderId(Long id) {
        jpaRepository.deleteById(id);
    }
}
