# Chapter. 2-4 서버구축-데이터베이스 심화

-----------------------------------------------------------------------------------------------------------------

### 낙관적 락 (**충돌이 없는경우 사용**)

- 데이터를 수정할때, 직접 락을 걸지 않는다.
- 버전(version)번호 등을 이용해서 충돌 감지
- 충돌이 발생하면 트랜잭션을 실패 시키거나 재시도 방식

### 비관적 락(Pessimistic Lock)

- 데이터를 읽거나  쓰기 전에 락을 먼저 건다
- 트렌잭션 종료될 때까지 락이 유지되므로 다른 트랙잭션은 해당 데이터에 접근 할 수 없음
    - 공유 락 (Shared Lock, S-Lock)
        - 읽기 작업 허용, 쓰기는 불가
        - 여러 트랜잭션이 동시에 읽을 수 있음
    - 배타 락 (Exclusive Lock, X-Lock)
        - 읽기/쓰기 모두 독점
        - 하나의 트랜잭션만 접근 가능

-----------------------------------------------------------------------------------------------------------------
**[ STEP 9 - Concurrency ]**

- 시나리오 별로, DB 구조별로 발생할 수 있는 동시성 이슈에 대해 테스트가 작성되었는지
- 동시성 이슈를 해결하기 위해 적합한 DB Lock 을 적용하였는지
- 동시성 이슈 분석 및 해결에 대해 보고서를 작성하였는지

    
URL 
- 코드 : [포인트 충전 동싱성 이슈 테스트 ](https://github.com/seokyeong-han/hh-repo/blob/5week/5week/e-commerce/src/test/java/com/example/ecommerce/integration/pointChargeTest.java),    커밋 : [2ed42ed](https://github.com/seokyeong-han/hh-repo/commit/2ed42edec6ec572b1073a75edba84bff20c9fc9a)
- 코드 : [쿠폰 선착순 발급 동시성 이슈 테스트](https://github.com/seokyeong-han/hh-repo/blob/5week/5week/e-commerce/src/test/java/com/example/ecommerce/integration/CouponIssueIntegrationTest.java), 커밋 : [ff89172](https://github.com/seokyeong-han/hh-repo/commit/ff89172784e9226104f3cc043b1798d73e264e99)
- 코드 : [주문, 재고차감 동시성  이슈 테스트](https://github.com/seokyeong-han/hh-repo/blob/5week/5week/e-commerce/src/test/java/com/example/ecommerce/integration/OrderIntergrationTest.java),  커밋 : [264d840](https://github.com/seokyeong-han/hh-repo/commit/264d840291d4b6c39328ea953522d0ae62d63e41)


**동시성 이슈 보고서**
<details>
<summary> 1. 선착순 쿠폰발급 동시성 테스트</summary>

### **선착순 쿠폰 발급 동시성 처리 개선 보고서**

### 📌 개요

실서비스에서 한정 수량의 쿠폰을 여러 유저가 동시에 발급 요청할 수 있다. 따라서 **동시 요청 환경에서 정합성을 보장하지 않으면 초과 발급, 중복 발급 등의 문제가 발생**할 수 있다.

본 사례에서는 **"선착순 쿠폰 발급 기능"** 을 테스트하며 발생한 동시성 문제를 **비관적 락(Pessimistic Lock, X-Lock)** 과 **재시도(@Retryable)** 기반으로 해결한 과정을 정리하였다.

---

### 🔍 문제 배경

- 쿠폰은 한정 수량으로 운영되며, 유저가 동시에 쿠폰을 발급받는 **동시 요청**이 발생할 수 있음.
- 예를 들어 쿠폰 수량이 10개일 때, 15명의 유저가 동시에 발급 요청 시 **중복 발급, 초과 발급** 가능성 존재.
- **발급 실패한 유저는 재시도가 없으면 발급 기회를 영영 잃음**

---

### ⚠️ 2. 원인 분석

- 쿠폰 객체를 `조회 → 검증 → 수정 → 저장` 하는 과정에서 **동시성 충돌 발생**
- 비관적 락으로 데이터 정합성은 확보했지만,
    - **커넥션 풀이 모두 점유되며 기다리던 요청들이 타임아웃 발생**
    - HikariPool-1 - Connection is not available, request timed out after 30033ms (total=15, active=15, idle=0, waiting=0) 와 같은 로그 발생
- 즉, **락은 성공적으로 동작했지만, 커넥션 부족으로 일부 유저는 발급 시도조차 실패**

---

### 🛠 3. 해결 방법

### ✅ 비관적 락(Pessimistic Lock, **Exclusive Lock**) 적용

- 쿠폰을 `SELECT ... FOR UPDATE` 형태로 조회하여 **다른 트랜잭션이 읽지도 못하도록 배타적 잠금**
- JPA 코드:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT c FROM CouponJpaEntity c WHERE c.id = :id")
Optional<CouponJpaEntity> findWithLockById(@Param("id") Long id);
```

- Service 계층에서 `couponRepository.findWithLockById()` 사용하여 안전한 수량 제어

---

### 🔁 @Retryable 기반 재시도 처리

- 커넥션 풀 부족이나 락 대기 타임아웃은 일시적 장애로 간주
- `@Retryable`을 통해 자동 재시도로 극복

```java
@Retryable(
    value = {
        PessimisticLockException.class,
        CannotAcquireLockException.class,
        SQLTransientConnectionException.class, // 커넥션 풀 부족
        SQLTransientException.class            // 락 타임아웃
    },
    maxAttempts = 3,
    backoff = @Backoff(delay = 150)
)
@Transactional
public void assignCouponToUser(Long couponId, Long userId) {
    ...
}
```

- 결과적으로 **락 타임아웃이나 커넥션 부족 발생 시, 재시도하여 성공 확률 향상**

## 사용한 락 종류

| 종류 | 설명 |
| --- | --- |
| **Exclusive Lock (X-Lock)** | `SELECT ... FOR UPDATE` 를 통해 획득되는 **배타 잠금**으로, 해당 레코드에 대해 **읽기/쓰기 모두 차단** |
| 적용 방식 | `@Lock(LockModeType.PESSIMISTIC_WRITE)` + JPQL 쿼리 사용 |

---

### ✅ 4. 결과

- 15명의 유저가 동시에 쿠폰을 요청해도 **최대 수량인 10장까지만 발급되도록 보장**
- `UserCoupon` 저장 수: 10
- `Coupon.issuedCount`: 10
- **초과 발급 없음**
- **락 대기 실패는 재시도를 통해 일부 성공으로 전환**
</details>
<details>
<summary> 2. 포인트 충전 동시성 테스트</summary>

## **포인트 충전 기능 동시성 처리 개선 보고서**

### 📌 개요

동시 요청 환경에서 포인트 충전 기능에 대한 **정합성 보장 문제**가 발생하였으며 **동시 요청 환경에서 정합성을 보장하지 않으면 초과 발급, 중복 발급 등의 문제가 발생**할 수 있다.

이를 **낙관적 락(Optimistic Lock)** 기반 재시도 처리로 해결한 사례를 공유합니다.

---

### 🔍 1. 문제 배경

- 유저 포인트 충전 기능에 대해 **멀티스레드 환경 테스트**를 진행하던 중, 충전 금액이 일부 누락되는 현상 발견
- 예: 1000원 충전을 10번 병렬로 수행 시, 기대 잔액 10,000원이 아닌 4,000 ~ 8,000원 사이의 잔액으로 저장되는 경우 발생

---

### ⚠️ 2. 원인 분석

- **원인: 동시성 충돌 및 정합성 미보장**
    - 여러 트랜잭션이 동시에 동일한 유저 데이터를 `조회 → 수정 → 저장`하는 과정에서
    - 최종적으로 **마지막에 저장한 값만 DB에 반영**되는 **Lost Update 문제 발생**
- 기존 JPA 설정에서는 충돌 여부를 판단할 수 없어 누락이 발생해도 감지 불가

---

### 🛠 3. 해결 방법

### ✅ 낙관적 락(Optimistic Lock) 적용

- `UserJpaEntity`에 `@Version` 필드를 추가하여 **버전 기반 충돌 감지**

    ```java
    @Version
    private Long version;
    ```

- `@Retryable`을 적용하여 충돌 시 **자동 재시도** 로직 구현

### 🔁 적용 코드 예시

```java
java
복사편집
@Retryable(
    value = { OptimisticLockingFailureException.class },
    maxAttempts = 10,
    backoff = @Backoff(delay = 100)
)
@Transactional
public void chargePoint(PointCommand command) {
    User user = userRepository.findById(command.getUserId())
        .orElseThrow(() -> new RuntimeException("유저가 없습니다."));

    user.charge(command.getAmount());
    User saved = userRepository.save(user);

    pointHistoryRepository.save(
        PointHistory.charge(saved.getId(), command.getAmount(), saved.getBalance())
    );
}
```

- `@Transactional` 내에서 충돌 발생 시 Hibernate가 `OptimisticLockingFailureException` 발생
- `@Retryable` 설정에 따라 최대 10회까지 재시도하며, 매 시도마다 **최신 데이터를 다시 조회**

---

### ✅ 4. 결과

- 동시 충전 요청 10건 모두 정상 반영
- 최종 잔액이 10,000원으로 기대값과 완전히 일치
- 테스트 로그 및 단위 테스트 모두 성공적으로 통과

</details>
<details>
<summary> 3. 주문, 재고 처리 동시성 테스트</summary>

## 📌 동시성 문제 및 해결 보고서
### 🔍 **1. 문제 상황**

- **문제:** 상품의 재고가 한정되어 있을 때, 동시에 여러 유저가 같은 상품을 주문할 수 있는 상황에서 **동시성 문제**가 발생했습니다.
    - 예를 들어, 상품의 재고가 1개일 때 두 명 이상의 유저가 동시에 같은 상품을 구매하려고 시도할 경우, **재고를 중복으로 차감**하는 문제가 발생할 수 있습니다. 이러한 문제는 데이터의 일관성을 해치고, 최종 결과에 오류를 유발할 수 있습니다.
- **문제 발생 시나리오:**
    1. 유저 A와 유저 B가 동시에 같은 상품을 주문하려고 시도.
    2. 두 유저 모두 상품의 재고를 확인하고, 차감을 시도하지만, **동시성 문제로 인해 재고 차감이 동시에 이루어짐**.
    3. 그 결과, 상품의 재고는 0개이어야 하지만, 두 유저 모두 상품을 주문할 수 있게 되어 **재고가 부정확하게 차감**되는 상황 발생.

---

### 🔐 **2. 해결 방법**

- **해결 전략:** 이 문제를 해결하기 위해 **비관적 락(Pessimistic Lock)**을 사용하여 재고를 차감하는 방법을 선택했습니다. 비관적 락은 데이터의 일관성을 보장하기 위해 다른 트랜잭션이 해당 데이터를 동시에 수정하지 못하도록 **잠금을 거는 방식**입니다.
    - 비관적 락을 사용하면, 첫 번째 유저가 상품을 주문할 때, 해당 상품에 **잠금**을 걸고, 두 번째 유저는 잠금이 해제될 때까지 대기하게 되어 **동시성 문제를 방지**할 수 있습니다.
    - 또한, 트랜잭션을 **@Transactional**로 관리하여 한 번의 트랜잭션 내에서 모든 데이터 변경이 안전하게 이루어지도록 보장합니다.
- **적용 방식:**
    1. **재고 차감 시 비관적 락**을 사용하여, 상품의 재고를 변경하기 전에 해당 상품에 **잠금을 걸어** 다른 트랜잭션이 동시에 재고를 변경하지 못하도록 처리합니다.
    2. 트랜잭션이 실패하거나 예외가 발생할 경우, **롤백 메커니즘**을 통해 재고 상태를 복원하고, 데이터의 일관성을 유지합니다.
    3. 예외 처리 및 롤백은 `try-catch`로 처리하여, **주문 실패 시 재고 복원**이 이루어지도록 합니다.

---

### 🛠 **3. 구현 세부 사항**

- **비관적 락(Pessimistic Lock) 적용:**
    - `productRepository.findWithPessimisticLockById(productId)`를 사용하여 **비관적 락**을 설정합니다. 이를 통해 **상품에 잠금을 걸고**, 다른 트랜잭션은 잠금이 해제될 때까지 기다리게 됩니다.

    ```java
    @Transactional
    public void tryDecreaseStock(Long productId, int quantity) {
        Product product = productRepository.findWithPessimisticLockById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
    
        product.deductStock(quantity); // 재고 차감
        productRepository.save(product); // 재고 차감 저장
    }
    ```

- **트랜잭션 롤백**:
    - `placeOrder` 메서드에서 **잔액 차감 후** 주문 처리 과정 중 예외가 발생하면, `productService.rollbackStock(prepared.getOrderItems())`를 통해 **재고 롤백**이 실행됩니다.
    - *`@Transactional`*을 사용하여, 트랜잭션 내에서 모든 작업을 처리하고, 예외가 발생하면 자동으로 롤백되어 데이터가 일관되게 유지됩니다.

    ```java
    @Transactional
    public void placeOrder(Long userId, PreparedOrderItems prepared) {
        try {
            User user = userService.deductBalance(userId, prepared.getTotalPrice()); // 잔액 차감
            Order order = new Order(user.getId(), prepared.getOrderItems()); // 주문 생성
            orderRepository.save(order); // 주문 저장
            orderItemRepository.saveAll(prepared.getOrderItems(), order.getId()); // 주문 아이템 저장
        } catch (Exception e) {
            productService.rollbackStock(prepared.getOrderItems()); // 예외 발생 시 재고 롤백
            throw e; // 예외를 다시 던져서 트랜잭션 롤백
        }
    }
    ```

- **주문 실패 시 재고 복원**:
    - 주문이 실패하면 **롤백 처리**가 자동으로 이루어집니다. 예를 들어, **잔액 부족**으로 주문이 실패할 경우, **재고 복원**이 트랜잭션 내에서 실행됩니다.

    ```java
    @Transactional
    public void rollbackStock(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = findById(item.getProductId());
            product.restoreStock(item.getQuantity()); // 재고 복원
            productRepository.save(product); // 재고 저장
        }
    }
    ```


---

### 📊 **4. 테스트 및 결과**

- **동시성 테스트**: 여러 유저가 동시에 상품을 주문하려고 시도할 때, **첫 번째 유저만 성공**하고 **두 번째 유저는 실패**하는지 확인하는 테스트를 작성했습니다.
    - 이 테스트는 `@Transactional`과 **비관적 락**을 적용한 결과, 하나의 유저만 재고를 차감하도록 **동시성 문제가 해결됨**을 확인했습니다.
- **재고 롤백 테스트**: 주문 처리 중 예외가 발생하면, **재고가 원래 상태로 복원**되는지 확인하는 테스트를 작성했습니다.
    - **주문 실패 시 재고 복원**이 정상적으로 이루어짐을 확인하였습니다.

---

### 🔧 **5. 결론**

- **비관적 락**을 적용하여 **동시성 문제**를 해결하고, 재고 차감 시 발생할 수 있는 **중복 차감** 문제를 방지했습니다.
- **트랜잭션 관리**를 통해 **주문 실패 시 재고 롤백**이 제대로 이루어지도록 구현하여, 데이터의 **일관성**을 유지했습니다.
- 이로 인해, **정상적인 주문 처리**와 **주문 실패 시 안전한 재고 복원**이 보장되었습니다.

</details>

-----------------------------------------------------------------------------------------------------------------
**[ STEP 10 - Finalize ]**

- 모든 API 가 동작 가능한 수준으로 구현되었는지
- 시나리오 별로 Filter, Interceptor 등 부가 로직 및 스케줄러 등이 구현되었는지

구현하지 못함 -> 어떤 시나리오를 개발할지 정리
    
    발급기간이 지난 쿠폰 자동 상태 변경 및 삭제 스케쥴러 
    Interceptor 특정 API 호출 시 히스토리 등록 
    ...

-----------------------------------------------------------------------------------------------------------------
### **리뷰 포인트(질문)**
1. 질문 : 주문시 재고차감, 주문 서비스를 분리하였는데 같은 서비스에서 코드를 개발 하는것이 좋을까요? 
   1. order facade 소스 url : [OrderFacade](https://github.com/seokyeong-han/hh-repo/blob/5week/5week/e-commerce/src/main/java/com/example/ecommerce/api/order/facade/OrderFacade.java#L22)

2. 질문 : 선착순 쿠폰 발급 동시성 테스트시 스래드를 15개로 늘리니 히카리 풀 이셉션이 발생하여 오류가 발생하였습니다.
         properties에 히카르 풀 사이즈를 변경하여 해결했지만 현업에서 많은 건이 동시에 들어오면 어떻게 처리해야할까요? 이래서 비관락을 잘 사용하지 않는것인지 궁금합니다.

-----------------------------------------------------------------------------------------------------------------
### **이번주 KPT 회고**
### 쿠폰 기능 추가
    쿠폰 선착순 발급 (구현)
        전체 금액 할인 쿠폰 만 기능 구현
    쿠폰 할인 적용(미구현)
        (추후 쿠폰 타입을 추가하여 상품별 쿠폰, 전체 쿠폰으로 수정 가능 하도록 수정 얘정)
        (추후 할인타입을 선택하여 금액할인, 퍼센트할인 수정 가능 하도록 수정 예정)

### Keep
<!-- 유지해야 할 좋은 점 -->
    모르겠다
    
### Problem
<!--개선이 필요한 점-->
    프로젝트 구조가 이상해서 다시 만들고 있다..
    domain, api, infrastructure 수정 진행

### Try
<!-- 새롭게 시도할 점 -->
    쿠폰 할인 기능 개발하기
    
  
