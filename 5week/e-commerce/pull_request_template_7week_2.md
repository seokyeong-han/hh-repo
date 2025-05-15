# Chapter. 3-2 대용량 트래픽&데이터 처리

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
