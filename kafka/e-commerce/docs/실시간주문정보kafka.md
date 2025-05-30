## 실시간 주문정보(이커머스) kafka 도입 보고서

### 실시간 주문정보(이커머스) kafka 메시지로 발행 프로세스

```jsx
[client]
    |        
    |▶ Kafka 발행: order.start 
    |
[stock service]
    |
    |▶ Kafka consume: order.start
    | 재고 차감 
    |▶ Kafka 발행: stock.success 
    |
[order service]
    |
    |▶ Kafka consume: stock.success
    | 주문 생성
    |▶ 성공 Kafka 발행: payment.request
    |▶ 실패 Kafka 발행: stock.rollback
    |
[payment service]
    |
    |▶ Kafka consume: payment.request
    | 잔액 차감 
    |▶ 성공 Kafka 발행: payment.success
    |▶ 실패 Kafka 발행: order.cencel -> stock.rollback
```
### Kafka 설정 분리 전략 →  Producer/Consumer 분리

Producer
- KafkaTemplate을 통해 이벤트를 발행
- 주문 생성, 재고 성공, 결제 요청 등의 이벤트 전송
- acks, retries, linger.ms 등을 설정하여 안정성 및 성능 조절 가능

Consumer
- @KafkaListener로 이벤트 수신
- 컨슈머 그룹 기반으로 수평 확장 가능
- max.poll.records, ack-mode, concurrency 등을 설정하여 처리량 제어

KafkaConsumerConfig : https://github.com/seokyeong-han/hh-repo/blob/step17/kafka/e-commerce/src/main/java/com/example/ecommerce/config/KafkaConsumerConfig.java

KafkaProducerConfig : https://github.com/seokyeong-han/hh-repo/blob/step17/kafka/e-commerce/src/main/java/com/example/ecommerce/config/KafkaProducerConfig.java

-----------------------------------------------------------------------------------------------------------------
### 서비스별 요약
```jsx
서비스	        |역할	        |Producer	                |Consumer
order-service	| 주문 생성	|order.created	                |payment.success, stock.success
stock-service	| 재고 처리	|stock.success, stock.rollback	|order.start, stock.rollback
payment-service	| 결제 처리	|payment.success, order.cancel, |stock.rollback	payment.request
```
-----------------------------------------------------------------------------------------------------------------

### 1. 📦 주문 시작 요청 → `orderController`

Kafka 메시지의 Key에 orderId를 써서 순서 보장 하기 위해 uuid로 고유 키 생성
```jsx
@PostMapping
public ResponseEntity<Void> createOrder(@RequestBody CreateOrderRequest request) {
log.info(":: Received request to create order");

        String orderId = UUID.randomUUID().toString(); // 주문 ID 직접 생성
        OrderStartEvent event = new OrderStartEvent(
                orderId,
                request.getUserId(),
                request.getItems().stream()
                        .map(i -> new ProductOrderItem(i.getProductId(), i.getQuantity()))
                        .toList()
        );
        
        orderProducer.send("order.start", event); 

        return ResponseEntity.ok().build();
    }
```
### 2. 🧱 재고 차감 → `stockService`

  - kafka consume            : `order.start`
  - 주문 성공 시 kafka produce : `stock.success`
  - 실패 시 : 알림 로그
```jsx
@KafkaListener(
topics = "order.start",
groupId = "stock-service",
containerFactory = "orderKafkaListenerContainerFactory"
)
public void consume(ConsumerRecord<String, OrderStartEvent> record) {
OrderStartEvent event = record.value();
String orderId = record.key(); // 또는 event.getOrderId() 사용 가능

        log.info("✅ [StockService] Received OrderStartEvent (orderId={}): {}", orderId, event);
        log.info("✅ Stock reduction completed for orderId={}", orderId);

        //재고 차감 service 호출
        try {
            List<ProductOrderItemMessage> orderItems = stockService.reduceStock(record.value(), record.key());
            kafkaTemplate.send("stock.success", orderId, new StockSuccessEvent(
                    orderId, event.userId(), orderItems
            ));
        } catch (Exception e) {
            log.error("재고 차감 실패: {}", e.getMessage());
        }
```

### 3. 🛒 주문 생성 → `OrderService`
 - kafka consume : `stock.success`
 - 재고 차감 성공 시 kafka produce : `payment.request`
 - 실패 시 : `stock.rollback`

```jsx
@KafkaListener(
            topics = "stock.success",
            groupId = "order-service",
            containerFactory = "stockSuccessKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, StockSuccessEvent> record) {
        StockSuccessEvent event = record.value();
        String orderId = record.key(); // 또는 event.getOrderId() 사용 가능

        log.info("✅ [OrderService] Received StockSuccessEvent (orderId={}): {}", orderId, event);

        try {
            //주문 생성 실행
            OrderResult result = orderService.placeOrder(event.userId(), event.items());
            //결제 이벤트 발행
            kafkaTemplate.send("payment.request", orderId, new PaymentRequestedEvent(
                    orderId, event.userId(), result.orderId(), result.totalPrice(), event.items()
            ));
            log.info(":: 주문 성공");
        }catch (Exception e){
            log.info(":: 주문 실패 -> 재고 롤백 event");
            // 재고 롤백 이벤트 발행
            kafkaTemplate2.send("stock.rollback", orderId, new StockRollbackEvent(
                    orderId, event.userId(), event.items()
            ));
        }
```

### 4. 💳 결제 처리 → `PaymentService`
- kafka consume : `payment.request`
- 결재 성공 시 kafka produce : `payment.success`
- 실패 시 : `order.cancel`, `stock.rollback`
```jsx

    @KafkaListener(
            topics = "payment.request",
            groupId = "payment-service",
            containerFactory = "paymentRequestKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, PaymentRequestedEvent> record) {
        PaymentRequestedEvent event = record.value();
        String orderId = record.key(); // 또는 event.getOrderId() 사용 가능

        log.info("✅ [PaymentService] Received PaymentRequestedEvent (orderId={}): {}", orderId, event);
        try {
            paymentService.paymentProcessor(event.userId(), event.totalAmount());
            log.info("결제 성공");
            kafkaTemplate.send("payment.success", orderId, new PaymentSuccessEvent());
        }catch (Exception e){
            log.info("결재 실패 :: 주문 취소, 재고롤백 이벤트 발행");
            //1.주문 취소 이벤트 발행
            kafkaTemplate2.send("order.cencel", orderId, new OrderCancelEvent(
                    orderId, event.resultOrderId(), event.userId()
            ));
            //2.재고 롤백 이벤트 발행
            kafkaTemplate3.send("stock.rollback", orderId, new StockRollbackEvent(
                    orderId, event.userId(), event.items()
            ));
        }
    }
```
### 5. 🔁  보상 트랜젝션 
1. 주문취소 : `order.cancel`
- 결재 실패 시 발행 
```jsx
@KafkaListener(
            topics = "order.cancel",
            groupId = "order-service",
            containerFactory = "orderCancelKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, OrderCancelEvent> record){
        OrderCancelEvent event = record.value();
        String orderId = record.key(); // 또는 event.orderId() 사용 가능

        log.warn(" 주문 취소 이벤트 수신 (orderId={}): {}", orderId, event);

        try {
            orderService.cancelOrder(event.resultOrderId(), event.userId());
        }catch (Exception e){
            log.error(" 주문 취소 실패 (orderId={}): {}", orderId, e.getMessage(), e);
        }
    }
```

2. 재고롤백 : `stock.rollback`
- 주문 생성 실패 시, 결재 실패 시 발행 
```jsx
@KafkaListener(
            topics = "stock.rollback",
            groupId = "stock-service",
            containerFactory = "stockRollbackKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, StockRollbackEvent> record) {
        StockRollbackEvent event = record.value();
        String orderId = record.key();

        log.warn("⛔ [StockService] Received StockRollbackEvent (orderId={}): {}", orderId, event);

        try {
            stockService.rollbackStock(event.items());
            log.info("✅ 재고 롤백 완료 (orderId={})", orderId);
        }catch (Exception e) {
            log.error(" 재고 롤백 실패 (orderId={}): {}", orderId, e.getMessage(), e);
        }

    }
```

-----------------------------------------------------------------------------------------------------------------
### 개선 및 확장 방향

수동 커밋 기반 Ack 처리 적용 
- 현재는 자동 커밋(enable.auto.commit=true) 구조로 메시지를 받자마자 커밋됨
- 이를 AckMode.MANUAL_IMMEDIATE 또는 AckMode.MANUAL로 전환하여 처리 성공 후에만 ack.acknowledge()로 명시적 커밋 가능

