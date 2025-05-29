## 실시간 주문정보(이커머스) kafka 도입 보고서

수정된 실시간 주문정보(이커머스) kafka 메시지로 발행 프로세스

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

-----------------------------------------------------------------------------------------------------------------
order controller

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

