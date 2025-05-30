package com.example.ecommerce.infrastructure.order.jpaRepository.orderItem;

import com.example.ecommerce.domain.order.entity.orderItem.OrderItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, Long> {
    @Modifying
    @Query("DELETE FROM OrderItemJpaEntity oi WHERE oi.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);
}
