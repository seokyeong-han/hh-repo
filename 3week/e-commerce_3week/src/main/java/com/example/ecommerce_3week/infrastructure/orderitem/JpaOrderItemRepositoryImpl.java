package com.example.ecommerce_3week.infrastructure.orderitem;

import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import com.example.ecommerce_3week.domain.orderitem.OrderItemRepository;
import com.example.ecommerce_3week.infrastructure.order.OrderJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaOrderItemRepositoryImpl implements OrderItemRepository {
    private final OrderItemJpaRepository jpaRepository;

    @Override
    public void saveAll(List<OrderItem> orderItems, OrderJpaEntity orderJpaEntity) {
        List<OrderItemJpaEntity> entities = orderItems.stream()
                .map(item -> toEntity(item, orderJpaEntity))
                .toList();

        jpaRepository.saveAll(entities); // JPA bulk insert
    }
    // 도메인 → JPA Entity
    private OrderItemJpaEntity toEntity(OrderItem orderItem, OrderJpaEntity orderJpaEntity) {
        return new OrderItemJpaEntity(
                orderJpaEntity,
                orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getPricePerItem()
        );
    }
}
