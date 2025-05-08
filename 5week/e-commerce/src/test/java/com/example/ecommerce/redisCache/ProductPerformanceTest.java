package com.example.ecommerce.redisCache;

import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.domain.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.StopWatch;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@ExtendWith(OutputCaptureExtension.class)
public class ProductPerformanceTest {
    private static final Logger log = LoggerFactory.getLogger(ProductPerformanceTest.class);

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        mysql.start(); // 컨테이너 먼저 시작
        //mySql
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private ProductService productService;
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private RedisConnectionFactory rcf;
    @Autowired
    private CacheManager cacheManager;


    @BeforeEach
    void setup() {
        log.info(":::: Starting product performance test start");
        /*for (long i = 1; i <= 10000; i++) {
            Product product = new Product(null, 1000L, 10);
            productService.save(product);
        }*/
        // ID=1인 상품을 하나만 저장
        productService.save(new Product(null, 1000L, 10));

        log.info(":::: Product save success.");
    }

    @Test
    void whenSecondCall_then_noSqlLogged(CapturedOutput output) {
        // 첫 번째 호출 → DB hit → SQL 로그가 나와야 함
        long startTime = System.currentTimeMillis();
        productService.findById(1L);
        long endTime = System.currentTimeMillis();
        System.out.println("캐시를 적용했을 때 시간(첫번 째 호출 시간): " + (endTime - startTime) + " ms");

        // 두 번째 호출 → 캐시 hit → SQL 로그가 없어야 함
        startTime = System.currentTimeMillis();
        productService.findById(1L);
        endTime = System.currentTimeMillis();
        String logs = output.getAll();
        System.out.println("캐시를 적용했을 때 시간(두번 째 호출 시간): " + (endTime - startTime) + " ms");

        // "Hibernate: select" 로 시작하는 로그 라인만 골라서 카운트
        long hibernateSelectCount = logs.lines()
                .filter(line -> line.trim().startsWith("Hibernate: select"))
                .count();

        // 오직 한 번만 SQL 로그가 찍혔는지 검증
        assertThat(hibernateSelectCount).isEqualTo(1L);
    }

    @Test
    void measureDbVsCache() throws InterruptedException {
        long id = 1L;
        int N = 5000;
        Cache cache = cacheManager.getCache("product:detail");

        StopWatch sw = new StopWatch();

        // ────────────────────────────────────────────────
        // 1) “오직 DB 조회만” 구간
        //    캐시 비우기 → findById() 반복 → 캐시에 절대 올라가지 않도록
        cache.clear();

        sw.start("db-lookup");
        for (int i = 0; i < N; i++) {
            // 매 호출마다 캐시를 evict 해 줘도 되지만, clear 한 번이면 충분합니다.
            productService.findById(id);
            cache.evict(id);   // 안전장치: 혹시라도 캐시에 올랐다면 제거
        }
        sw.stop();

        // ────────────────────────────────────────────────
        // 2) “오직 Redis 캐시” 구간
        //    워밍업 한 번 → 캐시에 올라가 있는 상태에서만 읽기
        cache.clear();
        productService.findById(id);  // → DB hit 후 캐시에 저장

        sw.start("cache-lookup");
        for (int i = 0; i < N; i++) {
            productService.findById(id);  // → 순수 Redis 읽기
        }
        sw.stop();

        log.info(":::: 성능 비교 결과\n" + sw.prettyPrint());
    }

}
