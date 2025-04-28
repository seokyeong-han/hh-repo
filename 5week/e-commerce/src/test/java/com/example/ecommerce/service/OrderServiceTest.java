package com.example.ecommerce.service;

import com.example.ecommerce.api.product.dto.PreparedOrderItems;
import com.example.ecommerce.domain.order.model.Order;
import com.example.ecommerce.domain.order.model.OrderItem;
import com.example.ecommerce.domain.order.repository.OrderItemRepository;
import com.example.ecommerce.domain.order.repository.OrderRepository;
import com.example.ecommerce.domain.order.service.OrderService;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.service.ProductService;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import com.example.ecommerce.domain.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceTest.class);


    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private UserRepository userRepository;
    private UserService userService;
    private OrderService orderService;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        orderRepository = mock(OrderRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        userService = new UserService(userRepository);
        orderService = new OrderService(userService, productService, orderRepository, orderItemRepository);
        //내가 userService에서 설정한 순서대로 선언해야합니다.
    }

    @Test
    @DisplayName("deductBalance: 잔액 부족 예외 발생")
    void deductBalance_insufficientBalance_throwsException() {
        // given
        User user = new User(1L, "testuser", 500L, null); // 잔액이 500L뿐임
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.deductBalance(user.getId(), 1000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액이 부족합니다.");
    }

    @Test
    @DisplayName("deductBalance: 차감 금액이 null이거나 0 이하일 경우 예외")
    void deductBalance_invalidAmount_throwsException() {
        // given
        User user = new User(1L, "testuser", 5000L, 1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.deductBalance(user.getId(), 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("금액이 올바르지 않습니다.");
    }



    @Test
    @DisplayName("placeOrder: 유저 잔액 차감 및 주문 저장 성공")
    public void placeOrder_success() {
        // given
        User user = new User(1L, "testuser", 10_000L, null);

        Product product1 = new Product(1L, 1000L, 10); // 재고 10개짜리
        Product product2 = new Product(2L, 2000L, 5);  // 재고 5개짜리

        List<OrderItem> orderItems = List.of(
                new OrderItem(1L, 1L, 1L, 2, 2000L, 1000L, LocalDateTime.now()),
                new OrderItem(2L, 1L, 2L, 1, 2000L, 2000L, LocalDateTime.now())
        );
        PreparedOrderItems prepared = new PreparedOrderItems(orderItems);

        //잔액 차감
        User deductedUser = new User(user.getId(), user.getUsername(), user.getBalance() - prepared.getTotalPrice(), null);
        when(userService.deductBalance(user.getId(), prepared.getTotalPrice())).thenReturn(deductedUser);
        //주문 저장
        Order mockOrder = new Order(1L, user.getId(), orderItems);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        // when  → 이걸 실행해야
        orderService.placeOrder(user.getId(), prepared);

        // then → 내부 동작이 잘 실행됐는지 확인
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order captured = captor.getValue();

        log.info("✅ 주문 유저 ID: {}", captured.getUserId());
        log.info("✅ 총 주문 금액: {}", captured.getTotalPrice());
        log.info("💸 주문 후 남은 유저 잔액 : {}", deductedUser.getBalance());

        for (OrderItem item : captured.getItems()) {
            log.info("📦 주문 아이템 → 상품ID: {}, 수량: {}, 총가격: {}, 단가: {}",
                    item.getProductId(),
                    item.getQuantity(),
                    item.getTotalPrice(),
                    item.getPricePerItem());
        }

        assertThat(captured.getUserId()).isEqualTo(user.getId());
        assertThat(captured.getTotalPrice()).isEqualTo(prepared.getTotalPrice());
        assertThat(captured.getItems()).hasSize(2);
        /*
            placeOrder는 여러 도메인 컴포넌트를 조합하는 핵심 로직이기 때문에, 단순히 하나만 호출하면 테스트가 어렵다.
            Mock을 잘 세팅해서 단위 테스트로 검증
            orderService.placeOrder(request.getUserId(), prepared); 이렇게 호출로 test하기 어렵다는거
         */
    }

}
