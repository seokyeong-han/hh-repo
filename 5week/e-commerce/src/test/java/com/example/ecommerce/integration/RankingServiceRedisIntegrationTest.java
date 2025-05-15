package com.example.ecommerce.integration;

import com.example.ecommerce.common.recode.DailyRanking;
import com.example.ecommerce.domain.ranking.entity.DailyRankingJpaEntity;
import com.example.ecommerce.domain.ranking.repository.RankingRepository;
import com.example.ecommerce.domain.ranking.service.RankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RankingServiceRedisIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(RankingServiceRedisIntegrationTest.class);
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2.4")
            .withExposedPorts(6379); // Redis 기본 포트 6379

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        mysql.start(); // 컨테이너 먼저 시작
        redis.start(); // Redis 컨테이너 시작

        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        registry.add("spring.data.redis.host", () -> redis.getHost());
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private RankingService rankingService;

    @Autowired
    private RankingRepository rankingRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private String todayKey;

    @BeforeEach
    void beforeEach() {
        // 매 테스트마다 Redis 날려버리고
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        // 오늘자 키 계산
        todayKey = "popular:order:daily:" +
                LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    }


    @Test
    void recordView_ShouldIncreaseZSetScore() {
        // given
        rankingService.recordView(1L); // 3회
        rankingService.recordView(1L);
        rankingService.recordView(1L);

        rankingService.recordView(2L); // 2회
        rankingService.recordView(2L);

        rankingService.recordView(3L); // 1회

        // when
        Set<ZSetOperations.TypedTuple<String>> entries =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(todayKey, 0, -1);

        // 디버깅용 로그
        System.out.println("🔥 Redis ZSet contents:");
        if (entries != null) {
            entries.forEach(e ->
                    System.out.printf("상품 ID: %s, 점수(조회순): %.0f%n", e.getValue(), e.getScore())
            );
        }

        // then
        assertThat(entries).isNotNull();
        LocalDate today = LocalDate.now();
        List<DailyRanking> rankings = entries.stream()
                .map(e -> new DailyRanking(
                        today,
                        Long.valueOf(e.getValue()),
                        0, // 테스트에서 랭크는 신경 안 쓰므로 0 또는 정렬 순서로 계산 가능
                        e.getScore().intValue()
                ))
                .toList();

        assertThat(rankings)
                .extracting(DailyRanking::score)
                .containsExactly(3, 2, 1);
    }

    @Test
    void persistDailyRanking_shouldStoreZSetToDatabase() {
        // given: 전날 기준으로 Redis에 ZSet 값 세팅
        LocalDate targetDate = LocalDate.now().minusDays(1);
        String key = "popular:order:daily:" + targetDate.format(DateTimeFormatter.BASIC_ISO_DATE);

        redisTemplate.opsForZSet().add(key, "1", 3);
        redisTemplate.opsForZSet().add(key, "2", 2);
        redisTemplate.opsForZSet().add(key, "3", 1);

        // when: 랭킹 저장 메서드 호출
        rankingService.persistDailyRanking(targetDate);

        // then: DB에 잘 저장됐는지 확인
        List<DailyRanking> saved = rankingRepository.findByDate(targetDate);
        assertThat(saved).hasSize(3);

        assertThat(saved)
                .extracting(DailyRanking::productId)
                .containsExactly(1L, 2L, 3L);

        assertThat(saved)
                .extracting(DailyRanking::score)
                .containsExactly(3, 2, 1);
    }

    @Test
    void cacheWeeklyRanking_shouldCacheWeeklyZSet() {
        // given: 지난 7일간 DB에 랭킹 데이터 저장
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(7);

        // 예: 7일간 productId=1은 매일 3점, 2는 2점, 3은 1점 → 총합: 21, 14, 7
        for (int i = 0; i < 7; i++) {
            LocalDate date = start.plusDays(i);
            List<DailyRanking> rankings = List.of(
                    new DailyRanking(today,1L, 1, 3),
                    new DailyRanking(today,2L, 2, 2),
                    new DailyRanking(today,3L, 3, 1)
            );
            rankingRepository.saveDaily(date, rankings);
        }

        // when: 캐싱 로직 실행
        rankingService.cacheWeeklyRanking();

        // then: Redis에 주간 랭킹 캐싱되었는지 확인
        String key = "popular:order:weekly";
        Set<ZSetOperations.TypedTuple<String>> entries =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);

        // 디버깅용 로그 추가
        System.out.println("🔥 Redis ZSet (주간 랭킹) contents:");
        if (entries != null) {
            entries.forEach(e ->
                    System.out.printf("📦 productId=%s, score=%.0f%n", e.getValue(), e.getScore())
            );
        }

        assertThat(entries).isNotNull();
        assertThat(entries).hasSize(3);

        // 점수 기준 정렬 검증 (productId 1이 가장 높음)
        List<DailyRanking> weekly = entries.stream()
                .map(e -> new DailyRanking(
                        today, // rankingDate는 테스트에서 중요하지 않으므로 today로 고정
                        Long.valueOf(e.getValue()),
                        0,
                        e.getScore().intValue()
                )).toList();

        assertThat(weekly)
                .extracting(DailyRanking::productId)
                .containsExactly(1L, 2L, 3L);

        assertThat(weekly)
                .extracting(DailyRanking::score)
                .containsExactly(21, 14, 7); // 3 * 7, 2 * 7, 1 * 7
    }



}