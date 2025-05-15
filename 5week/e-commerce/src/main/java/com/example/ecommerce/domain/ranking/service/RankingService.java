package com.example.ecommerce.domain.ranking.service;

import com.example.ecommerce.common.recode.DailyRanking;
import com.example.ecommerce.domain.order.service.OrderService;
import com.example.ecommerce.domain.ranking.repository.RankingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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

    private static final DateTimeFormatter DAILY_FMT = DateTimeFormatter.BASIC_ISO_DATE;// yyyyMMdd

    // — 키 생성 유틸 —
    private String dailyKey(LocalDate date) {
        return "popular:order:daily:" + date.format(DAILY_FMT);
    }

    //주문 발생시 랭킹 업데이트
    /**
     * 실시간: 오늘 일간 랭킹에만 1 증가
     */
    public void recordView(Long productId) {
        String key = dailyKey(LocalDate.now());
        redisTemplate.opsForZSet().incrementScore(key, productId.toString(), 1);
        // ZINCRBY popular:order:daily:20250514 1 "42"
        // 키 popular:order:daily:20250514 에서 멤버 "42" 의 점수를 1만큼 올린다.

        // TTL: 30일 치만 보관
        redisTemplate.expire(key, Duration.ofDays(8));
        // EXPIRE popular:order:daily:20250514 2592000
    }

    /**
     * 오늘의 Top N
     */
    public List<Long> getDailyTop(int limit) {
        return getTop(dailyKey(LocalDate.now()), limit);
    }


    private List<Long> getTop(String key, int limit) {
        Set<String> ids = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
        if (ids == null) return List.of();
        return ids.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    @Transactional
    public void persistDailyRanking(LocalDate date) {
        LocalDate today = LocalDate.now();//일별 랭킹 db insert
        String key = dailyKey(date);
        Set<ZSetOperations.TypedTuple<String>> top = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, 99);

        if (top == null) return;

        List<DailyRanking> rankings = new ArrayList<>();
        int rank = 1;

        for (ZSetOperations.TypedTuple<String> tuple : top) {
            Long productId = Long.valueOf(tuple.getValue());
            int score = tuple.getScore().intValue();
            rankings.add(new DailyRanking(today, productId, rank++, score));
        }

        // ✅ 로깅 추가
        log.info("✅ 일자: {}", date);
        log.info("✅ 저장할 랭킹 목록 ({}건):", rankings.size());
        for (DailyRanking r : rankings) {
            log.info("📦 productId={}, rank={}, score={}", r.productId(), r.rank(), r.score());
        }

        rankingRepository.saveDaily(date, rankings);

    }

    @Transactional
    public void cacheWeeklyRanking() {
        //오늘을 기준으로 전날까지 7일간의 랭킹을 조회
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(7);
        LocalDate end = today.minusDays(1);

        //7일간 일간 랭킹 조회
        List<DailyRanking> last7days = rankingRepository.findBetweenDates(start, end);

        // loging ///////////////
        log.info("📦 지난 7일간 랭킹 개수: {}", last7days.size());
        last7days.forEach(r ->
                log.info(" - RedisKey=popular:order:daily:{}, productId={}, rank={}, score={}",
                        r.rankingDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                        r.productId(), r.rank(), r.score())
        );
        ////////////////////////

        //점수 합산
        Map<Long, Integer> scoreMap = last7days.stream()
                .collect(Collectors.groupingBy(
                        DailyRanking::productId,
                        Collectors.summingInt(DailyRanking::score)
                ));

        // Redis 키 생성 및 초기화
        String weeklyKey = "popular:order:weekly";
        redisTemplate.delete(weeklyKey);

        // Redis용 ZSet 데이터 생성
        Set<ZSetOperations.TypedTuple<String>> tuples = scoreMap.entrySet().stream()
                .map(e -> new DefaultTypedTuple<>(e.getKey().toString(), (double) e.getValue()))
                .collect(Collectors.toSet());

        redisTemplate.opsForZSet().add(weeklyKey, tuples); //Redis에 ZADD 명령으로 저장
        redisTemplate.expire(weeklyKey, Duration.ofDays(2));
    }


}
