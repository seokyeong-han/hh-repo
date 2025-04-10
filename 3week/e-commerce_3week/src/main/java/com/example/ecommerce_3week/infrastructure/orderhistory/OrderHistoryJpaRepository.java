package com.example.ecommerce_3week.infrastructure.orderhistory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryJpaRepository extends JpaRepository<OrderHistoryJpaEntity, Long> {

}
