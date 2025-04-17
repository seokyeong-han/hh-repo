package com.example.ecommerce_3week.infrastructure.ponthistory;

import com.example.ecommerce_3week.domain.pointhistory.PointHistory;
import com.example.ecommerce_3week.domain.pointhistory.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaPointHistoryRepositoryImpl implements PointHistoryRepository {
    private final PointHistoryJpaRepository jpaRepository;

    @Override
    public void save(PointHistory history) {
        jpaRepository.save(toEntity(history));
    }

    @Override
    public List<PointHistory> findByUserId(Long userId) {
        // PointHistoryJpaEntity를 조회하고, 이를 PointHistory로 변환
        List<PointHistoryJpaEntity> entities = jpaRepository.findAllByUserId(userId);
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    private PointHistoryJpaEntity toEntity(PointHistory domain) {
        return new PointHistoryJpaEntity(
                domain.getUserId(),
                domain.getAmount(),
                domain.getType()
        );
    }

    private PointHistory toDomain(PointHistoryJpaEntity entity) {
        return new PointHistory(
                entity.getId(),
                entity.getUserId(),
                entity.getAmount(),
                entity.getType(),
                entity.getCreatedAt()
        );
    }

    /* 도메인 객체에 toDomain 넣도록 변경하기
    * public static User toDomain(UserChargeRequest request) {
        return new User(request.getUsername(), request.getBalance());
    }
    * */


}
