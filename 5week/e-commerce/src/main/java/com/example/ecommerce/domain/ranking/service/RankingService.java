package com.example.ecommerce.domain.ranking.service;

import com.example.ecommerce.domain.order.service.OrderService;
import com.example.ecommerce.domain.ranking.repository.RankingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RankingService {
    private static final Logger log = LoggerFactory.getLogger(RankingService.class);

    private final RankingRepository rankingRepository;

    private final RedisTemplate<String, String> redisTemplate;

    private static final DateTimeFormatter DAILY_FMT   = DateTimeFormatter.BASIC_ISO_DATE;// yyyyMMdd
    private static final WeekFields WEEK_FIELDS        = WeekFields.of(Locale.getDefault());

    // — 키 생성 유틸 —
    private String dailyKey(LocalDate date) {
        return "popular:order:daily:" + date.format(DAILY_FMT);
    }
    private String weeklyKey(LocalDate d)  {
        int w = d.get(WEEK_FIELDS.weekOfWeekBasedYear());
        return String.format("popular:weekly:%d-W%02d", d.getYear(), w);
    }

    //주문 발생시 랭킹 업데이트
    /** 실시간: 오늘 일간 랭킹에만 1 증가 */
    public void recordView(Long productId) {
        String key = dailyKey(LocalDate.now());
        redisTemplate.opsForZSet().incrementScore(key, productId.toString(), 1);
        // TTL: 30일 치만 보관
        redisTemplate.expire(key, Duration.ofDays(30));
    }

    /** 오늘의 Top N */
    public List<Long> getDailyTop(int limit) {
        return getTop(dailyKey(LocalDate.now()), limit);
    }

    /** 이번 주의 Top N (스케줄러가 미리 만들어둔 ZSET 사용) */
    public List<Long> getWeeklyTop(int limit) {
        return getTop(weeklyKey(LocalDate.now()), limit);
    }

    private List<Long> getTop(String key, int limit) {
        Set<String> ids = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
        if (ids == null) return List.of();
        return ids.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    @Transactional
    public void persistDailyRanking(LocalDate date) {
        String key = dailyKey(date);
        List<Long> topIds = getTop(key, 100);
        rankingRepository.saveDaily(date, topIds);
    }


}
