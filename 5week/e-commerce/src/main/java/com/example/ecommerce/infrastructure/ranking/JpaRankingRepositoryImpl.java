package com.example.ecommerce.infrastructure.ranking;

import com.example.ecommerce.common.recode.DailyRanking;
import com.example.ecommerce.domain.ranking.entity.DailyRankingJpaEntity;
import com.example.ecommerce.domain.ranking.repository.RankingRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class JpaRankingRepositoryImpl implements RankingRepository {
    private final RankingJpaRepository jpaRepository;

    @Override
    public void deleteByDate(LocalDate date) {
        jpaRepository.deleteByRankingDate(date);
    }

    @Override
    public void saveDaily(LocalDate date, List<DailyRanking> rankings) {
        // 1. 기존 랭킹 삭제
        deleteByDate(date);

        // 2. 새로운 엔티티로 변환해서 저장
        List<DailyRankingJpaEntity> entities = rankings.stream()
                .map(r -> new DailyRankingJpaEntity(date, r.productId(), r.rank(), r.score()))
                .toList();
        jpaRepository.saveAll(entities);
    }

    @Override
    public List<DailyRanking> findByDate(LocalDate date) {
        return jpaRepository.findByRankingDate(date).stream()
                .map(e -> new DailyRanking(e.getRankingDate(), e.getProductId(), e.getRank(), e.getScore()))
                .toList();
    }

    @Override
    public List<DailyRanking> findBetweenDates(LocalDate start, LocalDate end) {
        return jpaRepository.findBetweenDates(start, end);
    }
}
