package com.example.ecommerce.domain.order.event;

import com.example.ecommerce.api.order.dto.OrderCommand;
import com.example.ecommerce.domain.order.model.Order;
import com.example.ecommerce.domain.order.model.OrderItem;
import com.example.ecommerce.domain.order.repository.OrderItemRepository;
import com.example.ecommerce.domain.order.repository.OrderRepository;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.domain.product.service.ProductService;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final ProductService productService;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 주문 이벤트 수신 후 주문 처리 로직 수행
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlaced(OrderEvent event) {
        log.info(":: 주문 이벤트 수신 userId={}", event.getUserId());

        List<OrderCommand> commands = event.getOrderCommands();
        List<OrderItem> orderItems = new ArrayList<>();
        long totalPrice = 0L;

        //재고차감, 오더 아이템 설정
        for (OrderCommand command : commands) {
            Product product = productRepository.findWithPessimisticLockById(command.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

            product.deductStock(command.getQuantity());
            productRepository.save(product);

            long price = product.getPrice();
            long total = price * command.getQuantity();
            totalPrice += total;

            orderItems.add(new OrderItem(null, null, product.getId()
                    , command.getQuantity(), total, price, LocalDateTime.now()));
        }

        // 유저 잔액 차감
        User user = userService.deductBalance(event.getUserId(), totalPrice);

        // 주문 저장
        Order order = orderRepository.save(new Order(null, user.getId(), orderItems));
        orderItemRepository.saveAll(orderItems, order.getId());

        log.info(":: 주문 처리 완료: orderId={}, userId={}", order.getId(), user.getId());

        //주문 조회수 증가 이벤트 발행
        List<Long> productIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .toList();
        eventPublisher.publishEvent(new OrderPlacedEvent(productIds));

    }
}
