# Chapter. 2-3 서버구축-데이터베이스

- **정규화**: 데이터 중복을 줄이고 일관성을 높임 **(쓰기 성능 ↑)**
- **반정규화**: 일부 중복을 허용해 읽기 성능을 높임 **(읽기 성능 ↑)**

### Transaction(트랜젝션)
- **더 이상 나눌 수 없는 하나의 완결된 작업 단위**
- **모든 과정이 완료되어야 의미가 있는 작업의 최소 단위**
#### AICD중요
- **`Atomicity (원자성)` -** 트랜잭션 내 모든 작업은 전부 성공하거나, 전부 실패해야 한다. 일부만 반영된 상태는 허용되지 않는다.
- **`Consistency (일관성)` -** 트랜잭션 수행 전후로 시스템의 상태는 항상 일관성을 유지해야 한다. 예를 들어, 은행 계좌 간 이체에서 한 쪽 계좌는 차감되었는데, 다른 한 쪽은 입금되지 않았다면 일관성이 깨진 것이다.
- **`Isolation (고립성)` -** 동시에 실행되는 여러 트랜잭션이 서로 간섭하지 않아야 한다. 각 트랜잭션은 독립적으로 실행된 것처럼 보여야 한다.
- **`Durability (지속성)` -** 트랜잭션이 성공적으로 완료되었다면, 시스템 장애가 발생하더라도 그 결과는 보존되어야 한다.

-----------------------------------------------------------------------------------------------------------------
### STEP07 - Integration 
- Infrastructure Layer 작성
- 기능별 통합 테스트 작성
  - [재고부족 예외처리](https://github.com/seokyeong-han/hh-repo/commit/93bec3dacd2eb10e23070b3f5616e804e9903bd1) 
  - [잔액부족 예외처리](https://github.com/seokyeong-han/hh-repo/commit/0f81604442eadedcb7d571e1d7da02fa9a87500e)
  - [주문성공 통합테스트](https://github.com/seokyeong-han/hh-repo/commit/cdf8205c5614e7f42f094a18f81dfa228f47c281)
  - [충전 성공 통합테스트](https://github.com/seokyeong-han/hh-repo/blob/4week/3week/e-commerce_3week/src/test/java/com/example/ecommerce_3week/integration/UserChargeIntegrationTest.java)

### STEP08 - DB

-----------------------------------------------------------------------------------------------------------------
### **리뷰 포인트(질문)**

    저번주에 과제 피드백 중에 
    public void placeOrder(OrderRequest request){  
    -> 이부분은 파사드까지는 불필요해 보입니다. 
            User user = userService.findUserById(request.getUserId()); 의 요소가 repo로만 처리해도 될것으로 보임 -> 불필요하게 파사드 패턴을 사용하게 되면 소스의 개발 복잡성이 올라가고 유지보수가 힘들어 짐으로 반드시 필요한 곳에서만 사용되어야 합니다. (서비스로 처리가능한부분은 서비스단에서 모두 처리하는 것이 이득)
    부분을 수정해 봤는데 맞게 한건지 모르겠습니다.

    
