package com.example.ecommerce.integration;

import com.example.ecommerce.api.order.dto.OrderCommand;
import com.example.ecommerce.api.order.dto.OrderRequest;
import com.example.ecommerce.api.order.facade.OrderFacade;
import com.example.ecommerce.api.product.dto.PreparedOrderItems;
import com.example.ecommerce.domain.order.model.Order;
import com.example.ecommerce.domain.order.model.OrderItem;
import com.example.ecommerce.domain.order.repository.OrderItemRepository;
import com.example.ecommerce.domain.order.repository.OrderRepository;
import com.example.ecommerce.domain.order.service.OrderService;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.domain.product.service.ProductService;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderIntergrationTest {
    private static final Logger log = LoggerFactory.getLogger(OrderIntergrationTest.class);

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2.4")
            .withExposedPorts(6379); // Redis 기본 포트 6379

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        mysql.start(); // 컨테이너 먼저 시작
        redis.start(); // Redis 컨테이너 시작

        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        registry.add("spring.data.redis.host", () -> redis.getHost());
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));

    }

    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    /////////////////////////////
    /// 동시성 TEST
    /// ////////////////////////
    @Test
    void 동시_여러_유저_하나의_상품_주문_성공() throws InterruptedException {
        // given: 유저 10명 생성
        List<User> users = IntStream.range(0, 10)
                .mapToObj(i -> new User(null, "유저" + i, 5000L, null))
                .map(userRepository::save)
                .toList();

        Product p1 = productRepository.save(new Product(null, 5000L, 2)); // 재고 1개

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            final User user = users.get(i);
            executor.submit(() -> {
                try {
                    OrderRequest request = new OrderRequest(user.getId(),
                            List.of(new OrderRequest.OrderItemRequest(p1.getId(), 1)));
                    orderFacade.placeOrder(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.warn("주문 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then: 성공 갯수
        assertThat(successCount.get()).isEqualTo(2);
    }

    @Test
    void 동시_같은_상품_주문_1개_성공() throws InterruptedException {
        // given
        User user = userRepository.save(new User(null, "test유저", 10_000L, null)); // 잔액 부족 유도
        Product p1 = productRepository.save(new Product(null, 5000L, 1));
        Product p2 = productRepository.save(new Product(null, 3000L, 1));

        OrderRequest request = new OrderRequest(user.getId(),
                List.of(
                        new OrderRequest.OrderItemRequest(p1.getId(), 1)
                        ,new OrderRequest.OrderItemRequest(p2.getId(), 1)
                ));

        int threadCount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    orderFacade.placeOrder(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.warn("주문 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        assertThat(successCount.get()).isEqualTo(1);
    }
    ////////////////////////////
    @Test
    @Transactional
    @DisplayName("재고 차감 성공 후 주문 실패 시 재고 롤백된다")
    void rollbackStock_afterOrderFailure() {
        // given
        User user = userRepository.save(new User(null, "롤백유저", 5_000L, null)); // 잔액 부족 유도
        Product p1 = productRepository.save(new Product(null, 5000L, 3));

        List<OrderCommand> commands = List.of(
                new OrderCommand(p1.getId(), 2) // 총 10,000원 필요
        );

        // when
        assertThatThrownBy(() -> {
            PreparedOrderItems prepared = orderService.prepareOrderItems(commands); // 여기서 재고 차감
            orderService.placeOrder(user.getId(), prepared); // 여기서 잔액 부족으로 실패 -> 롤백
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잔액이 부족");

        // then: 재고가 원래대로 복원되었는지 확인
        Product updated = productRepository.findById(p1.getId()).orElseThrow();
        assertThat(updated.getStock()).isEqualTo(3); // 재고가 원복되었는지 확인

        log.info("✅ 주문 실패 후 재고 복원됨, 최종 재고: {}", updated.getStock());
    }

    @Test
    @DisplayName("비관적 락 - 동시에 재고 차감 시 하나만 성공")
    void testPessimisticLock() throws InterruptedException {
        // given
        Product saved = productRepository.save(new Product(null, 5000L, 1)); // 재고 1개

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<String> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    List<OrderCommand> commands = List.of(new OrderCommand(saved.getId(), 1));
                    orderService.prepareOrderItems(commands); // ✅ 새 서비스 메서드 호출
                    results.add("성공");
                } catch (Exception e) {
                    results.add("실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 결과 출력 및 검증
        long successCount = results.stream().filter("성공"::equals).count();
        long failCount = results.stream().filter(r -> r.startsWith("실패")).count();

        System.out.println("✅ 성공 수: " + successCount);
        System.out.println("❌ 실패 수: " + failCount);
        results.forEach(System.out::println);

        // 검증: 1번 성공, 4번 실패
        assertThat(successCount).isEqualTo(1);
        assertThat(failCount).isEqualTo(4);
    }

    @Test
    @DisplayName("주문 중 예외 발생 시 재고 롤백된다")
    void rollbackStock_onFailure() {
        // given
        User user = userRepository.save(new User(null, "롤백유저", 9_000L, null));
        Product p1 = productRepository.save(new Product(null, 5000L, 5));

        List<OrderItem> items = List.of(
                new OrderItem(null, null, p1.getId(), 2, 10_000L, 5000L, LocalDateTime.now())
        );
        PreparedOrderItems prepared = new PreparedOrderItems(items);

        log.info("🧪 테스트 시작 - 유저 ID: {}, 초기 잔액: {}", user.getId(), user.getBalance());
        log.info("📦 상품 ID: {}, 초기 재고: {}", p1.getId(), p1.getStock());

        // when: 강제로 잔액 부족 유도
        assertThatThrownBy(() -> {
            orderService.placeOrder(user.getId(), prepared);
        }).isInstanceOf(IllegalArgumentException.class);

        // then: 재고가 원래대로 복원되었는지 확인
        Product updated = productRepository.findById(p1.getId()).orElseThrow();
        assertThat(updated.getStock()).isEqualTo(5); // 재고 롤백 성공

        log.info("🧾 주문 중 예외 발생 후 확인");
        log.info("💸 유저 잔액 = {}", userRepository.findById(user.getId()).orElseThrow().getBalance());
        log.info("📦 상품 재고 복원 확인 = {}", updated.getStock());
    }


    @Test
    @DisplayName("주문 성공")
    void order_success() {
        // given
        User user = userRepository.save(new User(null, "통합유저", 20_000L, null));

        Product p1 = productRepository.save(new Product(null, 5000L, 10));
        Product p2 = productRepository.save(new Product(null, 2000L, 5));

        List<OrderCommand> commands = List.of(
                new OrderCommand(p1.getId(), 2),
                new OrderCommand(p2.getId(), 3)
        );
        // when
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderCommand command : commands) {
            Product prod = productRepository.findById(command.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            prod.deductStock(command.getQuantity()); // 재고 차감

            OrderItem item = new OrderItem(
                    null, // id
                    null, // orderId
                    prod.getId(),
                    command.getQuantity(),
                    prod.getPrice() * command.getQuantity(), // 총 가격
                    prod.getPrice(),
                    LocalDateTime.now()
            );

            orderItems.add(item);
            productRepository.save(prod); // 재고 차감 저장
        }

        PreparedOrderItems prepared = new PreparedOrderItems(orderItems);

        orderService.placeOrder(user.getId(), prepared);

        // then
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getBalance()).isEqualTo(20_000L - 16_000L); //유저 잔액이 차감 되었는지 확인

        Product updatedP1 = productRepository.findById(p1.getId()).orElseThrow();
        Product updatedP2 = productRepository.findById(p2.getId()).orElseThrow();
        assertThat(updatedP1.getStock()).isEqualTo(8);
        assertThat(updatedP2.getStock()).isEqualTo(2);

        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1); //오더가 1개 생겼는지 확인

        orderItems = orderItemRepository.findByOrderId(orders.get(0).getId());
        assertThat(orderItems).hasSize(2); //주문 아이템은 2개가 생성되었는지 확인

        log.info("💳 유저 잔액 = {}", updatedUser.getBalance());
        log.info("📦 상품1 재고 = {}", updatedP1.getStock());
        log.info("📦 상품2 재고 = {}", updatedP2.getStock());
        log.info("🧾 생성된 주문 수 = {}", orders.size());
        log.info("📑 주문 아이템 수 = {}", orderItems.size());
        orderItems.forEach(item ->
                log.info("🔹 상품 ID: {}, 수량: {}, 총가격: {}", item.getProductId(), item.getQuantity(), item.getTotalPrice())
        );
    }


}
