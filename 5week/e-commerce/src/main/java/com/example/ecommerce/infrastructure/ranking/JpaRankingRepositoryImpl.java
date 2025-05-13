package com.example.ecommerce.infrastructure.ranking;

import com.example.ecommerce.domain.ranking.entity.DailyRankingJpaEntity;
import com.example.ecommerce.domain.ranking.repository.RankingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
public abstract class JpaRankingRepositoryImpl implements RankingRepository {
    private final RankingJpaRepository jpaRepository;

    @Override
    public void deleteByDate(LocalDate date) {
        jpaRepository.deleteByRankingDate(date);
    }

    @Override
    public void saveDaily(LocalDate date, List<Long> topIds) {
        // 1. 기존 랭킹 삭제
        deleteByDate(date);

        // 2. 새로운 엔티티로 변환해서 저장
        List<DailyRankingJpaEntity> entities = IntStream.range(0, topIds.size())
                .mapToObj(i ->
                        new DailyRankingJpaEntity(
                                date,
                                topIds.get(i),
                                i + 1    // 순위는 1부터 시작
                        )
                )
                .toList();
        jpaRepository.saveAll(entities);
    }
}
