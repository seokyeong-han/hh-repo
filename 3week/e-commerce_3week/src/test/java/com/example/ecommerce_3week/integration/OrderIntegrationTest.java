package com.example.ecommerce_3week.integration;

import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.order.OrderRepository;
import com.example.ecommerce_3week.domain.orderhistory.OrderHistoryRepository;
import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import com.example.ecommerce_3week.domain.orderitem.OrderItemRepository;
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
import com.example.ecommerce_3week.service.product.ProductService;
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
    private OrderFacade orderFacade;

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
    private OrderHistoryRepository orderHistoryRepository;

    @BeforeEach
    void setup() {
        // 테스트용 유저 및 상품 세팅
        userRepository.deleteAll();
        productRepository.deleteAll();
        orderRepository.deleteAll();
        pointHistoryRepository.deleteAll();
        orderHistoryRepository.deleteAll();

    }

    @Test
    @Transactional
    void 주문_파사드_테스트_성공() {

    }

    @Test
    @Transactional
    void 주문_서비스_풀어서_테스트_성공() {
        //test 유저, 상품 등록
        User user = new User("testuser", 100L);
        userRepository.save(user);

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

        //orderService.createOrder(user, orderItems);
        Order order = new Order(user.getId(), orderItems);
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
