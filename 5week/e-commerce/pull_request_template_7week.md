# Chapter. 3-2 대용량 트래픽&데이터 처리

-----------------------------------------------------------------------------------------------------------------

### STEP 13 Ranking Design

    이커머스 시나리오    
    가장 많이 주문한 상품 랭킹을 Redis 기반으로 개발하고 설계 및 구현

-----------------------------------------------------------------------------------------------------------------
### 인기 주문 상품 랭킹 Redis 구성
![img_1.png](img_1.png)

#### 1.orderService에서 주문 진행 후 after-commit event 주문 수 증가
    
    orderService
    //주문 증가 이벤트 발행
            List<Long> productIds = prepared.getOrderItems().stream()
                    .map(OrderItem::getProductId)
                    .toList();
            eventPublisher.publishEvent(new OrderPlacedEvent(productIds));

    after-commit event
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        for (Long productId : event.getProductIds()) {
                rankingService.recordView(productId);
        }
    }

#### 2.일간 주문상품 랭킹 (Redis Sorted Set)

- key:`popular:order:daily:{yyyyMMdd}`

- value:Redis Sorted Set (ZSet)

- 각 요소: `productId` → 조회수 (score)
![img_3.png](img_3.png)


    //RankingService 실시간 주문 랭킹 증가
    public void recordView(Long productId) {
        String key = dailyKey(LocalDate.now());
        redisTemplate.opsForZSet().incrementScore(key, productId.toString(), 1);
    
        redisTemplate.expire(key, Duration.ofDays(8));
    }


    Redis key
    Key: popular:order:daily:20250514
    Sorted Set:
    - member: "42", score: 3
    - member: "17", score: 2
    - member: "8",  score: 1

    조회수 증가
    ZINCRBY popular:order:daily:20250514 1 "42"
    ttl 설정
    EXPIRE popular:order:daily:20250514 2592000

#### 3.주간 주문 상품 랭킹 (Redis Sorted Set)

 - key:`popular:order:weekly`
 - value:Redis Sorted Set (ZSet)
   - 각 요소: `productId` → 조회수 (score)
![img_4.png](img_4.png)

    
    주간 add redis
    ZADD popular:order:weekly 21 "1"
    ZADD popular:order:weekly 14 "2"
    ZADD popular:order:weekly 7 "3"

매일 Redis에 저장된 popular:order:daily:{yyyyMMdd} top100값 RDB에 저장 후

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

        rankingRepository.saveDaily(date, rankings);
    }

매일 지난 7일간 주간 상품 Redis cache 저장
    
    public void cacheWeeklyRanking() {
        //오늘을 기준으로 전날까지 7일간의 랭킹을 조회
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(7);
        LocalDate end = today.minusDays(1);

        //7일간 일간 랭킹 조회
        List<DailyRanking> last7days = rankingRepository.findBetweenDates(start, end);

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

-----------------------------------------------------------------------------------------------------------------
### STEP 14 Asynchronous Design

    이커머스 시나리오
    선착순 쿠폰발급 기능에 대해 Redis 기반의 설계를 진행하고, 적절하게 동작할 수 있도록 쿠폰 발급 로직을 개선해 제출

-----------------------------------------------------------------------------------------------------------------
1. 쿠폰 발급 대기열 등록 Redis (Redis Sorted Set)
   - key : `coupon:queue`
   - value : Redis Sorted Set(ZSet)
     - member : " `userId` : `couponId` " 
     - score : `System.currentTimeMillis();` -> 대기열 등록 시간

   
      public void requestCoupon(Long userId, Long couponId) {
         String queueKey = "coupon:queue";
         String value = userId + ":" + couponId;
         double score = System.currentTimeMillis(); // milliseconds timestamp

         redisTemplate.opsForZSet().add(queueKey, value, score);
      }   

2. 쿠폰 중복 발급 방지 Redis (Set)
   - Key: `coupon:issued:{couponId}`
   - Type: Redis Set 
   - Value: `userId` (String)


3. 쿠폰 재고 관리 Redis (String)
   - Key: `coupon:stock:{couponId}` 
   - Type: Redis String 
   - Value: 재고 수량 (예: "10")

스케줄러
[pull_request_template_7week.md](pull_request_template_7week.md)
      //5초마다 스케줄러로 쿠폰 대기열 확인
      @Scheduled(fixedDelay = 5000) // 5초마다 큐 처리
      public void processCouponQueue() {
         couponService.processQueue();
      }

쿠폰 발급

      public void processQueue() {
      String queueKey = "coupon:queue";
        // 최근 요청된 순서대로 최대 100개 처리
        Set<String> entries = redisTemplate.opsForZSet().range(queueKey, 0, 99);

        if (entries == null || entries.isEmpty()) return; //발급 대기 인원 없음

        for (String entry : entries) {
            String[] parts = entry.split(":");
            Long userId = Long.valueOf(parts[0]);
            Long couponId = Long.valueOf(parts[1]);

            // 중복 체크
            String issuedKey = "coupon:issued:" + couponId;
            if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(issuedKey, userId.toString()))) {
                continue;
            }

            // 재고 차감
            String stockKey = "coupon:stock:" + couponId;
            Long stock = redisTemplate.opsForValue().decrement(stockKey);
            if (stock == null || stock < 0) {
                redisTemplate.opsForValue().increment(stockKey); // 롤백
                continue;
            }

            // 중복 방지 등록 + 발급 처리
            redisTemplate.opsForSet().add(issuedKey, userId.toString());
            userCouponRepository.save(new UserCoupon(null, userId, couponId, false, LocalDateTime.now()));

            // ZSet에서 삭제
            redisTemplate.opsForZSet().remove(queueKey, entry);
        }
    }