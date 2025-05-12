package com.example.ecommerce.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PopularProductService {
    private final RedisTemplate<String, String> redisTemplate;
    // 일별 조회수로 키를 생성하고 배치를 사용하여 주별, 월별 인기상품 조회 할 수 있도록 한다.
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
        redisTemplate.opsForZSet().incrementScore(key, productId.toString(), 1); //ZSET 점수 증가
        redisTemplate.expire(key, Duration.ofDays(2));//TTL 설정: 2일 뒤 자동 만료
    }

    /**
     * 오늘의 인기 상품 ID 목록을 조회합니다.
     *
     * @param limit 가져올 상품 수
     * @return 조회수 기준 상위 인기 상품 ID 리스트
     */
    public List<Long> getTodayTopPopularProductIds(int limit) {
        String key = getTodayKey();
        Set<String> ids = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
        if (ids == null) return List.of();
        return ids.stream()
                .map(Long::parseLong)
                .toList();
    }

}
