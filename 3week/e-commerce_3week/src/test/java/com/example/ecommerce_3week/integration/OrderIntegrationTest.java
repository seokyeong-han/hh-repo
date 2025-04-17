package com.example.ecommerce_3week.integration;

import com.example.ecommerce_3week.common.enums.PointTransactionType;
import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.order.OrderRepository;
import com.example.ecommerce_3week.domain.orderhistory.OrderHistory;
import com.example.ecommerce_3week.domain.orderhistory.OrderHistoryRepository;
import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import com.example.ecommerce_3week.domain.orderitem.OrderItemRepository;
import com.example.ecommerce_3week.domain.pointhistory.PointHistory;
import com.example.ecommerce_3week.dto.order.facade.OrderFacadeRequest;
import com.example.ecommerce_3week.domain.pointhistory.PointHistoryRepository;
import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.product.ProductRepository;
import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.domain.user.UserRepository;
import com.example.ecommerce_3week.dto.order.controller.OrderRequest;
import com.example.ecommerce_3week.dto.order.facade.PreparedOrderItems;
import com.example.ecommerce_3week.facade.OrderFacade;
import com.example.ecommerce_3week.infrastructure.order.OrderJpaEntity;
import com.example.ecommerce_3week.service.order.OrderService;
import com.example.ecommerce_3week.service.orderhistory.OrderHistoryService;
import com.example.ecommerce_3week.service.pointhistory.PointHistoryService;
import com.example.ecommerce_3week.service.product.ProductService;
import com.example.ecommerce_3week.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.MySQLContainer;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Testcontainers
public class OrderIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private OrderHistoryService orderHistoryService;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private PointHistoryService pointHistoryService;

    private User testUser;
    private List<OrderFacadeRequest> itemRequests;
    @BeforeEach
    void setup() {
        // 테스트용 유저 및 상품 세팅
        userRepository.deleteAll();
        productRepository.deleteAll();
        orderRepository.deleteAll();
        pointHistoryRepository.deleteAll();
        orderHistoryRepository.deleteAll();

        // 테스트용 유저 저장 및 ID 할당
        testUser = new User("testuser", 60000L);
        testUser = userRepository.save(testUser); // 저장된 결과로 다시 할당
        // 테스트용 상품 주문 request 셋팅
        Product savedProduct1 = productRepository.save(new Product(10000L, 10));
        Product savedProduct2 = productRepository.save(new Product(5000L, 5));
        itemRequests = List.of(
                new OrderFacadeRequest(savedProduct1.getId(), 2),
                new OrderFacadeRequest(savedProduct2.getId(), 3));

    }


    @Test
    @Transactional
    void 주문_파사드_테스트_성공() {
        testUser = userService.findUserById(testUser.getId());
        System.out.println("testUser :: 이름 = "+testUser.getUsername()+", 잔액 = "+testUser.getBalance());

        //상품조회,재고차감,재고저장
        PreparedOrderItems prepared = productService.prepareOrderItems(itemRequests);

        // ✅ 값 확인
        System.out.println("총 주문 금액: " + prepared.getTotalPrice());
        System.out.println("=== 상품 목록 ===");
        for (Product product : prepared.getProducts()) {
            System.out.println("상품 ID: " + product.getId() + ", 재고: " + product.getStock());
        }

        //유저 잔액 차감, 저장
        userService.deductBalance(testUser, prepared.getTotalPrice());
        //주문생성, 주문 저장
        Order order = orderService.createOrder(testUser, prepared.getOrderItems());
        List<OrderItem> savedItems = orderItemRepository.findByOrderId(order.getId());

        // ✅ 값 확인2
        System.out.println("=== 주문 내역 ===");
        System.out.println("주문 ID:"+order.getId()+", 유저 ID: "+order.getUserId()+", 생성일: "+order.getCreatedAt());

        System.out.println("=== 주문 항목 목록 ===");
        for (OrderItem item : savedItems) {
            System.out.println("상품 ID: " + item.getProductId() + ", 수량: " + item.getQuantity() + ", 총액: " + item.getTotalPrice());
        }

        //주문 히스토리 저장
        orderHistoryService.save(testUser.getId(), order.getId(), prepared.getOrderItems());
        List<OrderHistory> orderHistories = orderHistoryRepository.findByOrderId(order.getId());
        // ✅ 값 확인3
        System.out.println("=== 주문 내역 ===");
        for (OrderHistory item : orderHistories) {
            System.out.println("history ID: "+item.getId()+", 주문 유저 ID: "+item.getUserId()+", 주문 ID: "+item.getOrderId()+", 상품 ID: "+item.getProductId()+" 구매수량: "+item.getQuantity()+", 구매 가격:"+item.getTotalPrice());
        }
        //포인트 히스토리 저장
        pointHistoryService.useSave(new PointHistory(testUser.getId(), prepared.getTotalPrice(), PointTransactionType.USE));
        List<PointHistory> pointHistories = pointHistoryRepository.findByUserId(testUser.getId());
        // ✅ 값 확인4
        System.out.println("=== 포인트 내역 ===");
        for(PointHistory item : pointHistories) {
            System.out.println("ID: "+item.getId()+", 타입: "+item.getType()+" 금액:"+item.getAmount());
        }

    }

    @Test
    void 주문_중_재고롤백() {
        // given
        User user = new User("testuser", 50000L);
        userRepository.save(user);

        Product savedProduct1 = productRepository.save(new Product(10000L, 10));
        Product savedProduct2 = productRepository.save(new Product(5000L, 5));

        // 주문 요청 생성
        List<OrderFacadeRequest> itemRequests = List.of(
                new OrderFacadeRequest(savedProduct1.getId(), 2),
                new OrderFacadeRequest(savedProduct2.getId(), 6) // 재고보다 많은 수량 주문
        );

        // when
        assertThatThrownBy(() -> {
            productService.prepareOrderItems(itemRequests); // 여기서 예외 발생해야 함
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 재고가 부족합니다.");

        // then - 재고가 롤백되었는지 확인
        Product after = productRepository.findById(savedProduct2.getId()).orElseThrow();
        assertThat(after.getStock()).isEqualTo(5); // 재고 원상복구

    }

    @Test
    void 유저_잔액부족_재고_롤백() {
        User user = new User("testuser", 50000L);
        userRepository.save(user);

    }

    @Test
    @Transactional
    void 주문_서비스_풀어서_테스트_성공() {
        //test 유저, 상품 등록
        User user = new User("testuser", 60000L);
        User saveUser = userRepository.save(user);

        Product savedProduct1 = productRepository.save(new Product(10000L, 10));
        Product savedProduct2 = productRepository.save(new Product(5000L, 5));

        // 상품 조회 (저장된 상품을 조회)
        Product foundProduct1 = productRepository.findById(savedProduct1.getId()).orElseThrow();
        Product foundProduct2 = productRepository.findById(savedProduct2.getId()).orElseThrow();

        // 상품 정보 출력 (직접 속성 값 출력)
        System.out.println("search Product 1 - ID: " + foundProduct1.getId() + ", Price: " + foundProduct1.getPrice() + ", Stock: " + foundProduct1.getStock());
        System.out.println("search Product 2 - ID: " + foundProduct2.getId() + ", Price: " + foundProduct2.getPrice() + ", Stock: " + foundProduct2.getStock());

        // 주문 요청 생성
        List<OrderFacadeRequest> itemRequests = List.of(
                new OrderFacadeRequest(savedProduct1.getId(), 2),
                new OrderFacadeRequest(savedProduct2.getId(), 1)
        );

        PreparedOrderItems preparedOrderItems = productService.prepareOrderItems(itemRequests);

        // 상품 재고가 차감되었는지 확인
        Product product1 = productRepository.findById(savedProduct1.getId()).orElseThrow();
        Product product2 = productRepository.findById(savedProduct2.getId()).orElseThrow();

        // 상품 정보 출력 및 검증
        System.out.println("Product 1 - ID: " + product1.getId() + ", Stock: " + product1.getStock());
        System.out.println("Product 2 - ID: " + product2.getId() + ", Stock: " + product2.getStock());

        assertThat(product1.getStock()).isEqualTo(8); //재고 차감 확인
        assertThat(product2.getStock()).isEqualTo(4);

        // OrderItem 검증
        List<OrderItem> orderItems = preparedOrderItems.getOrderItems();
        assertThat(orderItems).hasSize(2);

        //user 잔액 차감 및 검증 추가 --> 이거 해야함
        long totalPrice = preparedOrderItems.getTotalPrice(); // 주문 총액
        System.out.println("Total Price - " + totalPrice);
        //차감 하기
        userService.deductBalance(saveUser, totalPrice); //이거 하면 됨

        User user1 = userService.findUserById(saveUser.getId());
        System.out.println("user1 == " + user1.getBalance());


        //orderService.createOrder(user, orderItems);
        Order order = new Order(user1.getId(), orderItems);
        // order 저장
        Order saveOrder = orderRepository.save(order);

        //저장된 order 조회
        Order foundOrder = orderRepository.findById(saveOrder.getId()).orElseThrow();
        //저장된 order 출력
        System.out.println("search Order - ID: " + foundOrder.getId() + ", totalPrice: " + foundOrder.getTotalPrice());
        // 주문 검증
        assertThat(order).isNotNull();
        assertThat(order.getTotalPrice()).isEqualTo(
                (savedProduct1.getPrice() * 2) + (savedProduct2.getPrice() * 1)
        );

        //orderItems 저장
        orderItemRepository.saveAll(orderItems, foundOrder.getId());

        // OrderItem이 실제로 저장됐는지 확인
        List<OrderItem> savedItems = orderItemRepository.findByOrderId(foundOrder.getId());
        System.out.println("savedItems size: " + savedItems.size());
        savedItems.forEach(item -> System.out.println("Saved OrderItem: " + item.toString()));

        assertThat(savedItems).hasSize(2);
        assertThat(savedItems)
                .extracting("productId")
                .containsExactlyInAnyOrder(savedProduct1.getId(), savedProduct2.getId());



    }

}
