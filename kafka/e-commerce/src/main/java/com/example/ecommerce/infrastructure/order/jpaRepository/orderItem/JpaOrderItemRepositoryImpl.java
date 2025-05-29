package com.example.ecommerce.infrastructure.order.jpaRepository.orderItem;

import com.example.ecommerce.domain.order.entity.orderItem.OrderItemJpaEntity;
import com.example.ecommerce.domain.order.model.orderItem.OrderItem;
import com.example.ecommerce.domain.order.repository.orderItem.OrderItemRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaOrderItemRepositoryImpl implements OrderItemRepository {
    private final OrderItemJpaRepository jpaRepository;

    public JpaOrderItemRepositoryImpl(OrderItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<OrderItem> saveAll(List<OrderItem> orderItems, Long orderId) {
        List<OrderItemJpaEntity> entities = orderItems.stream()
                .map(item -> OrderItemJpaEntity.fromDomain(item, orderId))
                .toList();

        List<OrderItemJpaEntity> savedEntities = jpaRepository.saveAll(entities);

        return savedEntities.stream()
                .map(OrderItem::toDomain)
                .toList();
    }
}
