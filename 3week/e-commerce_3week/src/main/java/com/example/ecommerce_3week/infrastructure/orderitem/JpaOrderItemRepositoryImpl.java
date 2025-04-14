package com.example.ecommerce_3week.infrastructure.orderitem;

import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import com.example.ecommerce_3week.domain.orderitem.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaOrderItemRepositoryImpl implements OrderItemRepository {
    private final OrderItemJpaRepository jpaRepository;

    @Override
    public void saveAll(List<OrderItem> orderItems) {
        List<OrderItemJpaEntity> entities = orderItems.stream()
                .map(this::toEntity)
                .toList();
        jpaRepository.saveAll(entities); //JPA bulk insert
    }
    // 도메인 → JPA Entity
    private OrderItemJpaEntity toEntity(OrderItem orderItem) {
        return new OrderItemJpaEntity(
                orderItem.getOrderId(),
                orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getPricePerItem()
        );
    }
}
