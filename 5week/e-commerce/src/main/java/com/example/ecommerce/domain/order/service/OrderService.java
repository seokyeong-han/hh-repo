package com.example.ecommerce.domain.order.service;

import com.example.ecommerce.api.order.dto.OrderCommand;
import com.example.ecommerce.api.product.dto.PreparedOrderItems;
import com.example.ecommerce.common.recode.StockReserveRequest;
import com.example.ecommerce.common.recode.StockReserveRequestedEvent;
import com.example.ecommerce.common.recode.StockReservedItem;
import com.example.ecommerce.common.recode.StockRollbackRequestedEvent;
import com.example.ecommerce.domain.order.event.OrderPlacedEvent;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final UserService userService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PreparedOrderItems prepareOrderItems(List<OrderCommand> itemRequests) {
        //Product를 조회하고 재고를 차감한다 → stock-service의 책임
        //상품 가격을 이용해 OrderItem을 생성한다 → order-service의 책임

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderCommand command : itemRequests) {
            Product product = productRepository.findWithPessimisticLockById(command.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

            product.deductStock(command.getQuantity()); //도메인 차감
            productRepository.save(product);            //저장

            // 총 가격 계산
            long pricePerItem = product.getPrice();
            long totalPrice = pricePerItem * command.getQuantity();
            //주문 아이템 생성
            OrderItem item = new OrderItem(
                    null, null, product.getId(), command.getQuantity(),
                    totalPrice, pricePerItem, LocalDateTime.now());
            orderItems.add(item);

        }
        return new PreparedOrderItems(orderItems);
    }

    @Transactional // 핵심 도메인 로직 묶음
    public void placeOrder(Long userId, PreparedOrderItems prepared){
        try {
            // 유저 조회 + 잔액 차감 (낙관적 락 포함)
            User user = userService.deductBalance(userId, prepared.getTotalPrice());
            Order order = new Order(null, user.getId(), prepared.getOrderItems()); //total가격 저장
            // order 저장
            Order saveOrder = orderRepository.save(order);
            //order item 저장
            orderItemRepository.saveAll(prepared.getOrderItems(), saveOrder.getId());

            //주문 랭킹 증가 이벤트 발행
            List<Long> productIds = prepared.getOrderItems().stream()
                    .map(OrderItem::getProductId)
                    .toList();
            eventPublisher.publishEvent(new OrderPlacedEvent(productIds));

        } catch (Exception e) {
            log.warn("⚠ 주문 처리 실패. 재고 롤백 수행 중...");
            productService.rollbackStock(prepared.getOrderItems());
            log.warn("↩ 재고 롤백 완료");
            throw e;
        }
        //주문 히스토리 저장
        //포인트 사용 히스토리 저장
    }

    @Transactional
    public void eventPlaceOrder(Long userId, List<StockReservedItem> items){
        List<OrderItem> orderItems = new ArrayList<>();
        long totalPrice = 0L;

        //유저별 주문 id생성
        for(StockReservedItem item: items){
            //주문 아이템 생성
            orderItems.add(new OrderItem(null, null, userId, item.quantity(),
                    item.totalPrice(), item.pricePerItem(), LocalDateTime.now()
            ));

            totalPrice += item.totalPrice();
        }

        try {
            // 유저 조회 + 잔액 차감 (낙관적 락 포함) --> 이것도 이벤트로 발생?
            userService.deductBalance(userId, totalPrice);

            Order order = new Order(null, userId, orderItems); //total가격 저장
            // order 저장
            Order saveOrder = orderRepository.save(order);

            //order item 저장
            orderItemRepository.saveAll(orderItems, saveOrder.getId());

            //after-commit 주문 랭킹 증가 이벤트 발행
            List<Long> productIds = orderItems.stream()
                    .map(OrderItem::getProductId)
                    .toList();
            eventPublisher.publishEvent(new OrderPlacedEvent(productIds));

        } catch (Exception e) {
            log.error("주문 실패 :: 보상 트랙잭션 시작");
            log.error("1. 재고 롤백 이벤트 발행 ");
            List<StockReserveRequest> rollbackRequests = items.stream()
                    .map(i -> new StockReserveRequest(i.productId(), i.quantity()))
                    .toList();

            eventPublisher.publishEvent(new StockRollbackRequestedEvent(rollbackRequests));

            throw e;
        }
    }



}
