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
    public void saveAll(List<OrderItem> items, Long orderId) {
        OrderJpaEntity orderEntity = new OrderJpaEntity();  // 비어있는 객체
        orderEntity.setId(orderId);  // ID만 설정해서 참조

        List<OrderItemJpaEntity> entities = items.stream()
                .map(item -> new OrderItemJpaEntity(
                        orderEntity,
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPricePerItem()
                )).toList();

        jpaRepository.saveAll(entities);
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
