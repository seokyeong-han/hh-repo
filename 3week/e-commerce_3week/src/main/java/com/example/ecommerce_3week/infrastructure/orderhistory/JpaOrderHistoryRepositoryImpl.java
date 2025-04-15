package com.example.ecommerce_3week.infrastructure.orderhistory;

import com.example.ecommerce_3week.domain.orderhistory.OrderHistory;
import com.example.ecommerce_3week.domain.orderhistory.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@RequiredArgsConstructor
public class JpaOrderHistoryRepositoryImpl implements OrderHistoryRepository {
    private final OrderHistoryJpaRepository jpaRepository;

    @Override
    public void saveAll(List<OrderHistory> histories) {
        List<OrderHistoryJpaEntity> entities = histories.stream()
                .map(history -> new OrderHistoryJpaEntity(
                        history.getUserId(),
                        history.getProductId(),
                        history.getQuantity(),
                        history.getTotalPrice()
                )).toList();

        jpaRepository.saveAll(entities);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
