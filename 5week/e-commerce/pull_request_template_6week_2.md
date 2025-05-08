# Chapter. 3-1 대용량 트래픽&데이터 처리

-----------------------------------------------------------------------------------------------------------------

### STEP12 - Cache

- 조회가 오래 걸리거나, 자주 변하지 않는 데이터 등 애플리케이션의 요청 처리 성능을 높이기 위해 캐시 전략을 취할 수 있는 구간을 점검하고, 적절한 캐시 전략을 선정
- 위 구간에 대해 Redis 기반의 캐싱 전략을 시나리오에 적용하고 성능 개선 등을 포함한 보고서 작성 및 제출

-----------------------------------------------------------------------------------------------------------------
 - 스프링 캐시 기능과 Redis를 연동하기 위한 설정 클래스 redisConfig 설정 -> [RedisConfig.java](https://github.com/seokyeong-han/hh-repo/blob/5week/5week/e-commerce/src/main/java/com/example/ecommerce/global/config/RedisConfig.java)
 - @Cacheable, @CacheEvict .. 어노테이션 사용을 위한 RedisCacheManager 구성

### 1. 인기상품 조회 캐시

- 조회수 증가 및 인기 상품 조회 TEST -> [PopularProductServiceTest.java](https://github.com/seokyeong-han/hh-repo/blob/5week/5week/e-commerce/src/test/java/com/example/ecommerce/redisCache/PopularProductServiceTest.java)
- ZSET 구조를 활용하여 일별 조회수로 redis 조회수 기반 랭킹 캐시
- 스케줄러를 사용하여 주별, 월별 인기상품 조회 기능 개발 가능 


    //ZSET 키 생성
    private String getTodayKey() {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    }
상품 조회시 조회수 증가
    
    public void increaseViewCount(Long productId) {
        String key = getTodayKey();
        redisTemplate.opsForZSet().incrementScore(key, productId.toString(), 1); //ZSET 점수 증가
        redisTemplate.expire(key, Duration.ofDays(2));//TTL 설정: 2일 뒤 자동 만료
    }

productFacade api 호출로 인한 상품 조회 수 증가


    public ProductResponse getProductById(Long id) {
        Product product = productService.findById(id);
        //인기 상품 조회순 조회수 증가
        popularProductService.increaseViewCount(id);

        return ProductResponse.from(product);
    }

오늘 인기 상품 ID 목록을 조회

    public List<Long> getTodayTopPopularProductIds(int limit) {
        String key = getTodayKey();
        Set<String> ids = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
        if (ids == null) return List.of();
        return ids.stream()
                .map(Long::parseLong)
                .toList();
    }

### 2. 상품 조회 Cache

CacheConstants class를 사용하여 한곳에서 조회 redis key 관리

상품 조회 DB조회 RedisCache 조회 성능 TEST -> [ProductPerformanceTest.java](https://github.com/seokyeong-han/hh-repo/blob/5week/5week/e-commerce/src/test/java/com/example/ecommerce/redisCache/ProductPerformanceTest.java)

#### ProductService

상품 조회 service redis cache 셋팅

    @Cacheable(value = PRODUCT_DETAIL, key = "#id")
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

상품 저장 시 해당 product Id cache 제거
재고 롤백시 product Id cache 제거

    public void save(Product product) {
        Product saved = productRepository.save(product);
        //@CacheEvict사용시 최초 상품 등록시 캐시가 없어 오류가 나 저장된 목록을 캐시 삭제 하도록 수정
        cacheManager
                .getCache(PRODUCT_DETAIL)
                .evict(saved.getId());
    }

    //재고 롤백
    @Transactional
    public void rollbackStock(List<OrderItem> orderItems) { // 트랜잭션 내에서 롤백 처리
        for (OrderItem item : orderItems) {
            Product product = findById(item.getProductId());
            product.restoreStock(item.getQuantity());
            Product saved = productRepository.save(product);

            cacheManager
                    .getCache(PRODUCT_DETAIL)
                    .evict(saved.getId());
        }
    }

### 3. 쿠폰 조회 Cache

service 로직 개발 없이 SpringBootTest안에서 쿠폰 조회 DB조회 Redia조회 성능 비교 
-> [couponRedisCacheTest.java](https://github.com/seokyeong-han/hh-repo/blob/5week/5week/e-commerce/src/test/java/com/example/ecommerce/redisCache/couponRedisCacheTest.java)