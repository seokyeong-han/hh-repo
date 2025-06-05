package com.example.ecommerce.domain.ranking;

import com.example.ecommerce.api.ranking.dto.PopularProductDto;
import com.example.ecommerce.infrastructure.stock.consumer.StockConsumer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PopularProductService {
    private static final Logger log = LoggerFactory.getLogger(PopularProductService.class);
    //인기 상품 조회가 캐시, Redis, 통계, 랭킹 등 도메인 외부 관심사 기반일 경우
    private final RedisTemplate<String, String> redisTemplate;
    /**
     * 오늘자 ZSET 키 생성
     */
    private String getTodayKey() {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // "yyyyMMdd"
        return "popular:daily:" + today;
    }

    /**
     * 특정 상품의 오늘 조회수를 증가시킵니다.
     */
    public void increaseViewCount(Long productId) {
        String key = getTodayKey();
        Double newScore = redisTemplate.opsForZSet().incrementScore(key, productId.toString(), 1); //ZSET 점수 증가
        redisTemplate.expire(key, Duration.ofDays(2));//TTL 설정: 2일 뒤 자동 만료

        log.info("📈 [인기상품조회수 증가] key={}, productId={}, newScore={}", key, productId, newScore);
    }

    /**
     * 오늘의 인기 상품 ID 목록을 조회합니다.
     *
     * @param limit 가져올 상품 수
     * @return 조회수 기준 상위 인기 상품 ID 리스트
     */
    public List<PopularProductDto> getTodayTopPopularProductIds(int limit) {
        String key = getTodayKey();
        // ZSet에서 value와 score 함께 조회
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);

        if (tuples == null) return List.of();

        // DTO로 매핑
        return tuples.stream()
                .filter(tuple -> tuple.getValue() != null && tuple.getScore() != null)
                .map(tuple -> new PopularProductDto(
                        Long.parseLong(tuple.getValue()),
                        tuple.getScore().longValue()
                ))
                .toList();
    }
}
