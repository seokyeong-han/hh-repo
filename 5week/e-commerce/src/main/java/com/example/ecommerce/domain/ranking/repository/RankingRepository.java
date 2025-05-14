package com.example.ecommerce.domain.ranking.repository;



import com.example.ecommerce.common.recode.DailyRanking;

import java.time.LocalDate;
import java.util.List;

public interface RankingRepository {
    /** 기존 날짜별 랭킹을 모두 지운다. */
    void deleteByDate(LocalDate date);

    /** 특정 날짜의 Top N을 순위까지 포함해 저장한다. */
    void saveDaily(LocalDate date, List<DailyRanking> rankings);

    List<DailyRanking> findByDate(LocalDate date);

    List<DailyRanking> findBetweenDates(LocalDate start, LocalDate end);
}
