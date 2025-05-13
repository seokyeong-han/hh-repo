package com.example.ecommerce.infrastructure.scheduler;

import com.example.ecommerce.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RankingScheduler {
    private final RankingService rankingService;

    // 매일 자정
    @Scheduled(cron = "0 0 0 * * *")
    public void persistDaily() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        rankingService.persistDailyRanking(yesterday);
    }

    // 매주 월요일 자정
//    @Scheduled(cron = "0 0 0 * * MON")
//    public void persistWeekly() {
//        LocalDate weekStart = LocalDate.now().minusWeeks(1)
//                .with(DayOfWeek.MONDAY);
//        rankingService.persistWeeklyRanking(weekStart);
//    }
}
