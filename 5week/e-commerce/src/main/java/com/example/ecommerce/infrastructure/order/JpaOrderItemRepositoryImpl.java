package com.example.ecommerce.infrastructure.order;

import com.example.ecommerce.domain.coupon.entity.CouponJpaEntity;
import com.example.ecommerce.domain.coupon.model.Coupon;
import com.example.ecommerce.domain.order.entity.OrderItemJpaEntity;
import com.example.ecommerce.domain.order.model.OrderItem;
import com.example.ecommerce.domain.order.repository.OrderItemRepository;
import com.example.ecommerce.domain.product.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaOrderItemRepositoryImpl implements OrderItemRepository {
    private final OrderItemJpaRepository jpaRepository;

    public JpaOrderItemRepositoryImpl(OrderItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId).stream()
                .map(OrderItemJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderItem> saveAll(List<OrderItem> orderItems, Long orderId) {
        List<OrderItemJpaEntity> entities = orderItems.stream()
                .map(item -> OrderItemJpaEntity.fromDomain(item, orderId))
                .toList();

        List<OrderItemJpaEntity> savedEntities = jpaRepository.saveAll(entities);

        return savedEntities.stream()
                .map(OrderItemJpaEntity::toDomain)
                .toList();
    }
}
