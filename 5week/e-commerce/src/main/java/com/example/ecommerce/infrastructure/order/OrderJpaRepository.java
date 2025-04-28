package com.example.ecommerce.infrastructure.order;

import com.example.ecommerce.domain.order.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity,Long> {
}
