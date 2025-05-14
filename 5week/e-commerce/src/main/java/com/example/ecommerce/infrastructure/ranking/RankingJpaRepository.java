package com.example.ecommerce.infrastructure.ranking;

import com.example.ecommerce.common.recode.DailyRanking;
import com.example.ecommerce.domain.ranking.entity.DailyRankingJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RankingJpaRepository extends JpaRepository<DailyRankingJpaEntity, Long> {
    // deleteBy + 필드명 으로 자동으로 DELETE 쿼리 생성
    void deleteByRankingDate(LocalDate rankingDate);

    List<DailyRankingJpaEntity> findByRankingDate(LocalDate date);

    @Query("SELECT new com.example.ecommerce.common.recode.DailyRanking(dr.rankingDate, dr.productId, dr.rank, dr.score) " +
            "FROM DailyRankingJpaEntity dr " +
            "WHERE dr.rankingDate BETWEEN :start AND :end")
    List<DailyRanking> findBetweenDates(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
