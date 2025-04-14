package com.example.ecommerce_3week;

import com.example.ecommerce_3week.domain.order.OrderItem;
import com.example.ecommerce_3week.domain.orderhistory.OrderHistory;
import com.example.ecommerce_3week.domain.orderhistory.OrderHistoryRepository;
import com.example.ecommerce_3week.domain.pointhistory.PointHistoryRepository;
import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.service.orderhistory.OrderHistoryService;
import com.example.ecommerce_3week.service.pointhistory.PointHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderHistoryServiceTest {

    private OrderHistoryService orderHistoryService;
    private OrderHistoryRepository orderHistoryRepository;

    @BeforeEach
    void setUp() {
        orderHistoryRepository = mock(OrderHistoryRepository.class);
        orderHistoryService = new OrderHistoryService(orderHistoryRepository);
    }
        
    //이건 Mockito방식
    /*@Mock
    private OrderHistoryRepository orderHistoryRepository;

    @InjectMocks
    private OrderHistoryService orderHistoryService;*/

    @Test
    @DisplayName("save: 주문 아이템을 주문 히스토리로 변환 후 저장")
    void saveOrderHistory_success() {
        // given
        User user = new User(1L, "testUser",10_000L);
        OrderItem item1 = new OrderItem(1L, 2, 1000L); // 총 2000
        OrderItem item2 = new OrderItem(2L, 1, 3000L); // 총 3000
        List<OrderItem> items = List.of(item1, item2);

        // when
        orderHistoryService.save(user, items);

        // then
        // List<OrderHistory> 타입의 인자를 캡처할 준비를 함
        ArgumentCaptor<List<OrderHistory>> captor = ArgumentCaptor.forClass(List.class);
        //save(user, items) 호출 시 내부적으로 orderHistoryRepository.saveAll(...)에 전달된 인자가 정말 List<OrderHistory>
        // 타입인지 확인하고,그 값 자체를 "캡처해서" 꺼내 쓰기 위해 사용하는 코드
        verify(orderHistoryRepository, times(1)).saveAll(captor.capture()); //saveAll 1번 호출했는지

        List<OrderHistory> savedHistories = captor.getValue();
        assertThat(savedHistories).hasSize(2);

        OrderHistory h1 = savedHistories.get(0);
        assertThat(h1.getUserId()).isEqualTo(user.getId());
        assertThat(h1.getProductId()).isEqualTo(1L);
        assertThat(h1.getQuantity()).isEqualTo(2);
        assertThat(h1.getTotalPrice()).isEqualTo(2000L);

        OrderHistory h2 = savedHistories.get(1);
        assertThat(h2.getProductId()).isEqualTo(2L);
        assertThat(h2.getTotalPrice()).isEqualTo(3000L);
    }
}
