package com.example.ecommerce_3week.infrastructure.ponthistory;

import com.example.ecommerce_3week.domain.pointhistory.PointHistory;
import com.example.ecommerce_3week.domain.pointhistory.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPointHistoryRepositoryImpl implements PointHistoryRepository {
    private final PointHistoryJpaRepository jpaRepository;

    @Override
    public void save(PointHistory history) {
        jpaRepository.save(toEntity(history));
    }

    private PointHistoryJpaEntity toEntity(PointHistory domain) {
        return new PointHistoryJpaEntity(
                domain.getUserId(),
                domain.getAmount(),
                domain.getType()
        );
    }
}
