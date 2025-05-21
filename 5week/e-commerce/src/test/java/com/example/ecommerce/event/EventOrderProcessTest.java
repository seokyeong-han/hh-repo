package com.example.ecommerce.event;

import com.example.ecommerce.common.recode.ProceedOrderEvent;
import com.example.ecommerce.common.recode.StockReserveRequest;
import com.example.ecommerce.common.recode.StockReservedItem;
import com.example.ecommerce.domain.order.model.Order;
import com.example.ecommerce.domain.order.model.OrderItem;
import com.example.ecommerce.domain.order.repository.OrderItemRepository;
import com.example.ecommerce.domain.order.service.OrderService;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.domain.product.service.ProductService;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestEventConfig.class) //@TestConfiguration + @SpyBean	테스트 전용 설정에서 수동으로 빈 주입	✅ Spring 최신 권장 방식
public class EventOrderProcessTest {
    private static final Logger log = LoggerFactory.getLogger(EventOrderProcessTest.class);

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
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private TestProceedOrderEventListener listener;

    @Test
    void 재고차감_이벤트_정상발행_확인() {
        // given
        Product product = new Product(null, 1000L, 1); // 재고 1개
        Product saveProduct = productRepository.save(product);

        StockReserveRequest request = new StockReserveRequest(saveProduct.getId(), 1);

        // when
        productService.reserveStockEvent(1L, List.of(request));

        // then
        assertThat(listener.getReceivedEvents()).hasSize(1);

        ProceedOrderEvent event = listener.getReceivedEvents().get(0);
        assertThat(event.userId()).isEqualTo(1L);
        assertThat(event.items()).hasSize(1);
        assertThat(event.items().get(0).productId()).isEqualTo(saveProduct.getId());
    }

    @Test
    void 재고차감_후_주문생성_동작_확인() {
        // given: 상품, 유저 생성
        Product product = new Product(null, 1000L, 10);
        product = productRepository.save(product);

        User user = new User(null, "tester", 10_000L, 0L);
        user = userRepository.save(user);

        // 재고 예약 요청
        StockReservedItem item = new StockReservedItem(product.getId(), 2, product.getPrice(), product.getPrice() * 2);

        // when
        orderService.eventPlaceOrder(user.getId(), List.of(item));

        // then
        // 주문 저장됐는지 검증
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(1L);
        for (OrderItem orderItem : orderItems) {
            log.info("🧾 주문 아이템 - orderId: {}, productId: {}, quantity: {}, pricePerItem: {}, totalPrice: {}",
                    orderItem.getOrderId(), orderItem.getProductId(), orderItem.getQuantity(), orderItem.getPricePerItem(), orderItem.getTotalPrice());
        }
        assertThat(orderItems).hasSize(1);
        assertThat(orderItems.get(0).getProductId()).isEqualTo(product.getId());

        // 잔액 차감 검증
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        log.info("주문 후 유저 정보 - userId: {}, userBalance: {} ",updatedUser.getId(), updatedUser.getBalance());
        assertThat(updatedUser.getBalance()).isEqualTo(10_000L - (product.getPrice() * 2));
    }



}
