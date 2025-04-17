package com.example.ecommerce_3week.service;

import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import com.example.ecommerce_3week.domain.order.OrderRepository;
import com.example.ecommerce_3week.domain.orderitem.OrderItemRepository;
import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.dto.order.facade.OrderFacadeRequest;
import com.example.ecommerce_3week.dto.order.facade.PreparedOrderItems;
import com.example.ecommerce_3week.service.order.OrderService;
import com.example.ecommerce_3week.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    private ProductService productService;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        orderRepository = mock(OrderRepository.class);
        orderService = new OrderService(orderRepository, orderItemRepository );
    }

    @Test
    @DisplayName("prepareOrderItems: 재고 차감 및 주문 아이템 준비 성공")
    void prepareOrderItems_success() {
        // given
        OrderFacadeRequest request1 = new OrderFacadeRequest(1L, 2); //상품 id: 1L, 갯수: 2
        OrderFacadeRequest request2 = new OrderFacadeRequest(2L, 1);
        List<OrderFacadeRequest> requests = List.of(request1, request2);

        Product product1 = new Product(1L, 1000L, 10); //가짜(mock) 상품정보
        Product product2 = new Product(2L, 2000L, 5);

        when(productService.getProductById(1L)).thenReturn(product1); //orderService.prepareOrderItems 호출시 getProductById 미리 셋팅
        when(productService.getProductById(2L)).thenReturn(product2);

        // when
        PreparedOrderItems prepared = productService.prepareOrderItems(requests);

        // then
        assertThat(prepared.getProducts()).hasSize(2); //상품 아이템 갯수 2개 인지 확인
        assertThat(prepared.getOrderItems()).hasSize(2);

        assertThat(prepared.getOrderItems().get(0).getProductId()).isEqualTo(1L);
        assertThat(prepared.getOrderItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(prepared.getOrderItems().get(0).getPricePerItem()).isEqualTo(1000L);

        // 추가된 재고 차감 검증
        assertThat(product1.getStock()).isEqualTo(8); // 10 - 2
        assertThat(product2.getStock()).isEqualTo(4); // 5 - 1

        verify(productService, times(1)).getProductById(1L);
        verify(productService, times(1)).getProductById(2L);
    }

    @Test
    @DisplayName("createOrder: 주문 생성 성공")
    void createOrder_success () {
        // given
        User user = new User(1L, "testUser", 5000L);
        OrderItem item1 = new OrderItem(1L, 2, 1000L);
        OrderItem item2 = new OrderItem(2L, 1, 2000L);
        List<OrderItem> items = List.of(item1, item2);

        // when
        Order order = orderService.createOrder(user, items);

        // then
        assertThat(order.getUserId()).isEqualTo(user.getId());
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getTotalPrice()).isEqualTo(4000L); // 2*1000 + 1*2000
    }

}
