package com.example.ecommerce.infrastructure.order.jpaRepository.order;

import com.example.ecommerce.domain.order.entity.order.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {
}
