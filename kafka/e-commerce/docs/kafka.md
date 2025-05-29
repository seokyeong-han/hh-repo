# Kafka 정리

-----------------------------------------------------------------------------------------------------------------
## kafka란
### `"생산자(Producer)가 보낸 메시지를, 소비자(Consumer)가 안정적으로 받아가는 중간 시스템"`
- 분산 스트리밍 플랫폼
- 실시간으로 데이터를 주고받고 처리하기 위한 시스템
- 서비스 간에 데이터를 안정적이고 빠르게 주고받을 수 있게 해주는 시스템
-----------------------------------------------------------------------------------------------------------------
### kafka 주요 개념
Message
- 카프카에서 취급하는 데이터 단위(key, message)형태

Producer
- 메세지를 카프카 브로커에 발행 하는 서비스

Consumer
- 카프카 브로커에 적제된 메세지를 소비
- 메세지를 읽을 때 마다 파티션 별로 offset유지하며 처리했던 메세지 위치 추적 가능
- consumer-offset
컨슈머가 어디까지 처리 했는지 나타내는 offset
동일 메세지 처리 (x), 처리하지 않은 메세지 건너뛰기 (x) 마지막까지 처리한 offset을 저장(커밋)해야함
만약 오류가 발생하거나 문제가 발생할 경우, 컨슈머 그룹 차원에서 `--reset-offsets`  옵션을 통해 특정시점으로 offset을 되돌릴 수 있음

Broker
- Producer의 메세지를 받아 offset 지정 후 디스크에 저장
- Consumer의 파티션 Read에 응답해 디스크의 메세지 전송
- `Cluster` 내에서 각 1개씩 존재하는 Role Broker

Topic
- 데이터의 주제를 나타내며, 이름으로 분리된 로그입니다. 메시지를 보낼 때는 특정 토픽을 지정

Key
- 메시지를 특정 파티션에 고정하기 위한 값

Partition
- 토픽은 하나 이상의 파티션으로 나누어질 수 있으며, 각 파티션은 순서가 있는 연속된 메시지의 로그입니다. 파티션은 병렬 처리를 지원하고, 데이터의 분산 및 복제를 관

ex) send 예제

```jsx
kafkaTemplate.send("order-events", "order-123", 주문이벤트);
```
토픽 (Topic) 
  - 어떤 이벤트인지 구분 (ex. order-events, user-events)

Key
  -  "order-123"

파티션 (Partition)
  - Kafka가 미리 정해놓는 물리 저장 단위
-----------------------------------------------------------------------------------------------------------------
### kafka Docker 설치 및 테스트 방법
docker-compose.yml 파일 저장
```jsx
kafka:
image: bitnami/kafka:3.6
container_name: kafka-dev
ports:
- "9092:9092"
environment:
- KAFKA_CFG_PROCESS_ROLES=broker,controller
- KAFKA_CFG_NODE_ID=1
- KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=1@kafka-dev:9093
- KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
- KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093
- KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
- KAFKA_CFG_INTER_BROKER_LISTENER_NAME=PLAINTEXT
- KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER
- KAFKA_KRAFT_CLUSTER_ID=LpSKhSLNTfOOir1Qx8VjYg
- KAFKA_CFG_LOG_DIRS=/bitnami/kafka/data
volumes:
- kafka-data:/bitnami/kafka
volumes:
kafka-data:
```
기존 컨테이너/볼륨 정리 (초기화할 경우) 및 실행
```jsx
기존 컨테이너 및 kafka data 제거
  docker-compose down -v
  docker volume rm kafka-data
다시 실행 → 실행할 docker-compose.yml 폴더 안에서 실행
  docker-compose up -d
```
### kafka SpringBoot 테스트 방법

build.gradle → kafka 추가
```jsx
implementation 'org.springframework.kafka:spring-kafka'
```
cousumer 추가
```jsx
package com.example.ecommerce.infrastructure.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MyKafkaConsumer {
  @KafkaListener(topics = "test-topic", groupId = "test-group")
  public void listen(String message) {
  System.out.println("📥 Received: " + message);
}
}
```

producer 추가
```jsx
package com.example.ecommerce.infrastructure.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MyKafkaProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;

  public MyKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
  this.kafkaTemplate = kafkaTemplate;
}

public void send(String topic, String message) {
  kafkaTemplate.send(topic, message);
  System.out.println("📤 Sent: " + message);
}
}
```
호출 test controller 추가 
```jsx
@Controller
@RequestMapping("/kafka")
public class kafkaController {
  private final MyKafkaProducer producer;

  public kafkaController(MyKafkaProducer producer) {
  this.producer = producer;
}

@GetMapping("/send")
public String send(@RequestParam String message) {
  producer.send("test-topic", message);
  return "Message sent!";
}
}
```