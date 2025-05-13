package com.example.ecommerce.infrastructure.ranking;

import com.example.ecommerce.domain.ranking.entity.DailyRankingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface RankingJpaRepository extends JpaRepository<DailyRankingJpaEntity, Long> {
    // deleteBy + 필드명 으로 자동으로 DELETE 쿼리 생성
    void deleteByRankingDate(LocalDate rankingDate);
}
