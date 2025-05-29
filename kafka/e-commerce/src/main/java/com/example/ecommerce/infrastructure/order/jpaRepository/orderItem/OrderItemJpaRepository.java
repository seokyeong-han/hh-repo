package com.example.ecommerce.infrastructure.order.jpaRepository.orderItem;

import com.example.ecommerce.domain.order.entity.orderItem.OrderItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, Long> {
}
