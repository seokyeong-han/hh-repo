# Chapter. 3-3 대용량 트래픽&데이터 처리

-----------------------------------------------------------------------------------------------------------------
### **STEP 15** Application Event

- 실시간 주문정보(이커머스) & 예약정보(콘서트)를 데이터 플랫폼에 전송(mock API 호출)하는 요구사항을 이벤트를 활용하여 트랜잭션과 관심사를 분리하여 서비스를 개선합니다.

-----------------------------------------------------------------------------------------------------------------

기존 하나의 프로젝트에서 여러 도메인 서비스를 호출할 수 있던 Facade 방식에서 
MSA, Kafka 도입을 준비하며 Facade를 제거하고 Api Call -> service -> event 로직 변경

기능 분리
    
    - 재고차감
    - 주문생성, 잔액차감

saga 패턴(보상 트랜젝션)
    
    - 지금 로직으로는 재고 차감 후에 주문 생성되어 재고 롤백 로직 

기능 TEST commit : 


### Api call

    public ResponseEntity<Void> placeOrderEvent(@RequestBody OrderRequest request) {
        List<StockReserveRequest> reserveRequests = request.getItems().stream()
                .map(i -> new StockReserveRequest(i.getProductId(), i.getQuantity()))
                .toList();

        productService.reserveStockEvent(request.getUserId(), reserveRequests); //재고차감
        return ResponseEntity.ok().build();
    }


### 재고차감 service

    @Transactional
    public void reserveStockEvent(Long userId, List<StockReserveRequest> requests) {
        List<StockReservedItem> result = new ArrayList<>();
        for (StockReserveRequest req : requests) {
            Product product = productRepository.findWithPessimisticLockById(req.productId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + req.productId()));

            product.deductStock(req.quantity());
            productRepository.save(product);

            long total = product.getPrice() * req.quantity();
            result.add(new StockReservedItem(product.getId(), req.quantity(), product.getPrice(), total));
        }

        //주문 이벤트 호출 after commit
        eventPublisher.publishEvent(new ProceedOrderEvent( //주문 이벤트 발행
                userId, result
        ));

    }

### orderEvent

    public class OrderEventHandler {
    private final OrderService orderService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProceedOrderEvent event) {
        orderService.eventPlaceOrder(event.userId(), event.items());
    }

### 주문 service 

    @Transactional
    public void eventPlaceOrder(Long userId, List<StockReservedItem> items){
    List<OrderItem> orderItems = new ArrayList<>();
    long totalPrice = 0L;

        //유저별 주문 id생성
        for(StockReservedItem item: items){
            //주문 아이템 생성
            orderItems.add(new OrderItem(null, null, userId, item.quantity(),
                    item.totalPrice(), item.pricePerItem(), LocalDateTime.now()
            ));

            totalPrice += item.totalPrice();
        }

        try {
            // 유저 조회 + 잔액 차감 (낙관적 락 포함) --> 이것도 이벤트로 발생?
            userService.deductBalance(userId, totalPrice);

            Order order = new Order(null, userId, orderItems); //total가격 저장
            // order 저장
            Order saveOrder = orderRepository.save(order);

            //order item 저장
            orderItemRepository.saveAll(orderItems, saveOrder.getId());

            //after-commit 주문 랭킹 증가 이벤트 발행
            List<Long> productIds = orderItems.stream()
                    .map(OrderItem::getProductId)
                    .toList();
            eventPublisher.publishEvent(new OrderPlacedEvent(productIds));

        } catch (Exception e) {
            log.error("주문 실패 :: 보상 트랙잭션 시작");
            log.error("1. 재고 롤백 이벤트 발행 ");
            List<StockReserveRequest> rollbackRequests = items.stream()
                    .map(i -> new StockReserveRequest(i.productId(), i.quantity()))
                    .toList();

            eventPublisher.publishEvent(new StockRollbackRequestedEvent(rollbackRequests));

            throw e;
        }

-----------------------------------------------------------------------------------------------------------------
수정할 점
    
    주문 서비스 진행 시 재고차감 로직이 먼저 실행되어 도메인 이 별루? 인거 같다
    주문 -> 재고차감 -> 잔액차감 순으로 변경 예정
    saga 패턴도 변경 되어야함
    
    

질문 

    제가 프로젝트 레이어를 api, domin, infrastructure 로 나누었는데 멘토링 시간에 eventListener을 interface단에 두라고 하셔서
    밑에처럼 레이어를 밑에처럼 변경하는 것이 좋을까요?
    api -> controller, dto ...
    domain -> service, repository ...
    interface -> eventListener을
    infrastructure -> JpaRepository, JpaRepositoryImpl ...
    common -> 