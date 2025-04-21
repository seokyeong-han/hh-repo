package com.example.ecommerce.infrastructure.point;

import com.example.ecommerce.domain.point.entity.PointHistoryJpaEntity;
import com.example.ecommerce.domain.point.model.PointHistory;
import com.example.ecommerce.domain.point.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPointHistoryRepositoryImpl implements PointHistoryRepository {
    private final PointHistoryJpaRepository jpaRepository;

    @Override
    public void save(PointHistory pointHistory) {
        // PointHistory 도메인 객체를 JPA 엔티티로 변환
        PointHistoryJpaEntity entity = PointHistoryJpaEntity.fromDomain(pointHistory);

        // 변환된 JPA 엔티티를 JPA repository를 통해 저장
        jpaRepository.save(entity);
    }


}
