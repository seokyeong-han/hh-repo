package com.example.ecommerce_3week.infrastructure.orderitem;

import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import com.example.ecommerce_3week.domain.orderitem.OrderItemRepository;
import com.example.ecommerce_3week.infrastructure.order.OrderJpaEntity;
import com.example.ecommerce_3week.infrastructure.order.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaOrderItemRepositoryImpl implements OrderItemRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository jpaRepository;

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId).stream()
                .map(OrderItem::toDomain) // toDomain()으로 변환
                .collect(Collectors.toList());
    }

    public void saveAll(List<OrderItem> items, Long orderId) {
        OrderJpaEntity orderEntity = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // OrderItem -> OrderItemJpaEntity로 변환
        List<OrderItemJpaEntity> entities = items.stream()
                .map(item -> toEntity(item, orderEntity))
                .collect(Collectors.toList());

        // OrderItemJpaEntity 저장
        jpaRepository.saveAll(entities);

        // 저장된 OrderItemJpaEntity 확인
        //entities.forEach(entity -> System.out.println("Saved OrderItemJpaEntity: " + entity.toString()));

        // 저장된 OrderItemJpaEntity를 데이터베이스에서 확인
        //List<OrderItemJpaEntity> savedEntities = jpaRepository.findByOrderId(orderId); // orderId로 저장된 엔티티 조회
        //savedEntities.forEach(entity -> System.out.println("Saved OrderItem from DB: " + entity.toString()));
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
