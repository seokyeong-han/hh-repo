package com.example.ecommerce_3week.infrastructure.orderitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, Long> {
    @Query("SELECT oi FROM OrderItemJpaEntity oi WHERE oi.order.id = :orderId")
    List<OrderItemJpaEntity> findByOrderId(@Param("orderId") Long orderId);
}
