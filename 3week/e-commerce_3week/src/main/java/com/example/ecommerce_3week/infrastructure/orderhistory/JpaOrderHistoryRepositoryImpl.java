package com.example.ecommerce_3week.infrastructure.orderhistory;

import com.example.ecommerce_3week.domain.orderhistory.OrderHistory;
import com.example.ecommerce_3week.domain.orderhistory.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
public class JpaOrderHistoryRepositoryImpl implements OrderHistoryRepository {
    private final OrderHistoryJpaRepository jpaRepository;

    @Override
    public List<OrderHistory> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId).stream()
                .map(OrderHistory::toDomain)
                .collect(toList());
    }

    @Override
    public void saveAll(List<OrderHistory> histories) {
        List<OrderHistoryJpaEntity> entities = histories.stream()
                .map(this::toEntity)
                .toList();

        jpaRepository.saveAll(entities);
        //저장 확인
        //entities.forEach(entity -> System.out.println("Saved OrderHistoryJpaEntity: " + entity.toString()));
        //List<OrderHistoryJpaEntity> savedEntities = jpaRepository.findByOrderId(1L); // orderId로 저장된 엔티티 조회
        //savedEntities.forEach(entity -> System.out.println("Saved OrderHistoryJpaEntity from DB: " + entity.toString()));

    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    public OrderHistoryJpaEntity toEntity(OrderHistory history) {
        return new OrderHistoryJpaEntity(
                history.getOrderId(),
                history.getUserId(),
                history.getProductId(),
                history.getQuantity(),
                history.getTotalPrice()
        );
    }

}
