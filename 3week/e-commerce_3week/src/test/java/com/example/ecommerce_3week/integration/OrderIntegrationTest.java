package com.example.ecommerce_3week.integration;

import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.order.OrderRepository;
import com.example.ecommerce_3week.domain.orderhistory.OrderHistoryRepository;
import com.example.ecommerce_3week.domain.pointhistory.PointHistoryRepository;
import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.product.ProductRepository;
import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.domain.user.UserRepository;
import com.example.ecommerce_3week.dto.order.controller.OrderRequest;
import com.example.ecommerce_3week.facade.OrderFacade;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;

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
@Transactional
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
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @BeforeEach
    void setup() {
        // 테스트용 유저 및 상품 세팅
        //userRepository.deleteAll();
        //productRepository.deleteAll();
        //orderRepository.deleteAll();
        //pointHistoryRepository.deleteAll();
        //orderHistoryRepository.deleteAll();
        //test 유저, 상품 등록
        User user = new User("testuser", 100L);
        userRepository.save(user);

        Product product = new Product(10000L, 10);
        productRepository.save(product);
        Product product2 = new Product(5000L, 5);
        productRepository.save(product2);
    }

    @Test
    void 통합_주문_성공_테스트() {
        // given
        User user = userRepository.findAll().get(0);
        List<Product> products = productRepository.findAll();
        Product product1 = products.get(0);
        Product product2 = products.get(1);

        OrderRequest.OrderItemRequest item1 = new OrderRequest.OrderItemRequest();
        item1.setProductId(product1.getId());  // 상품 1의 ID
        item1.setQuantity(1);

        OrderRequest.OrderItemRequest item2 = new OrderRequest.OrderItemRequest();
        item2.setProductId(product2.getId());  // 상품 2의 ID
        item2.setQuantity(2);                   // 수량 2

        OrderRequest orderRequest = new OrderRequest(user.getId(), List.of(item1, item2));

        // 출력 예시
        System.out.println("UserId: " + orderRequest.getUserId());
        orderRequest.getItems().forEach(item -> {
            System.out.println("ProductId: " + item.getProductId() + ", Quantity: " + item.getQuantity());
        });

        // when
        orderFacade.placeOrder(orderRequest); // 실제 주문 호출
        // 주문이 저장되었는지 확인
        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);

        Order order = orders.get(0);
        assertThat(order.getUserId()).isEqualTo(user.getId());
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getTotalPrice()).isEqualTo(
                product1.getPrice() * 1 + product2.getPrice() * 2
        );
        //여기서부터 다시 test
        // order생성되었는지 orderitem생성되었는지 체크 먼저
        //order item 저장 확인


        // 유저 잔액 감소 확인
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getBalance()).isEqualTo(100L - order.getTotalPrice());

        // 재고 감소 확인
        Product updatedProduct1 = productRepository.findById(product1.getId()).orElseThrow();
        Product updatedProduct2 = productRepository.findById(product2.getId()).orElseThrow();
        assertThat(updatedProduct1.getStock()).isEqualTo(product1.getStock() - 1);
        assertThat(updatedProduct2.getStock()).isEqualTo(product2.getStock() - 2);

        // 포인트 히스토리 저장 확인
        List<PointHistory> pointHistories = pointHistoryRepository.findAll();
        assertThat(pointHistories).hasSize(1);
        assertThat(pointHistories.get(0).getAmount()).isEqualTo(order.getTotalPrice());

        // 주문 히스토리 저장 확인
        List<OrderHistory> orderHistories = orderHistoryRepository.findAll();
        assertThat(orderHistories).hasSize(2); // 상품 2개



    }
}
