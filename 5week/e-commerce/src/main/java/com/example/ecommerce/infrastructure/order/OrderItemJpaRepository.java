package com.example.ecommerce.infrastructure.order;

import com.example.ecommerce.domain.order.entity.OrderItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity,Long> {
    List<OrderItemJpaEntity> findByOrderId(Long orderId);
}
