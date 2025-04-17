package com.example.ecommerce_3week.infrastructure.orderhistory;

import com.example.ecommerce_3week.domain.orderhistory.OrderHistory;
import com.example.ecommerce_3week.infrastructure.orderitem.OrderItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderHistoryJpaRepository extends JpaRepository<OrderHistoryJpaEntity, Long> {
    @Query("SELECT oi FROM OrderHistoryJpaEntity oi WHERE oi.orderId = :orderId")
    List<OrderHistoryJpaEntity> findByOrderId(@Param("orderId") Long orderId);
}
