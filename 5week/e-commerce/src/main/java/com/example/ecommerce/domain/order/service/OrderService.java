package com.example.ecommerce.domain.order.service;

import com.example.ecommerce.api.product.dto.PreparedOrderItems;
import com.example.ecommerce.domain.order.model.Order;
import com.example.ecommerce.domain.order.repository.OrderItemRepository;
import com.example.ecommerce.domain.order.repository.OrderRepository;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.domain.product.service.ProductService;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final UserService userService;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional // 핵심 도메인 로직 묶음
    public void placeOrder(Long userId, PreparedOrderItems prepared){
        try {
            // 유저 조회 + 잔액 차감 (낙관적 락 포함)
            User user = userService.deductBalance(userId, prepared.getTotalPrice());
            Order order = new Order(user.getId(), prepared.getOrderItems()); //total가격 저장
            // order 저장
            Order saveOrder = orderRepository.save(order);
            //order item 저장
            orderItemRepository.saveAll(prepared.getOrderItems(), saveOrder.getId());
        } catch (Exception e) {
            log.warn("⚠ 주문 처리 실패. 재고 롤백 수행 중...");
            productService.rollbackStock(prepared.getOrderItems());
            log.warn("↩ 재고 롤백 완료");
            throw e;
        }
        //주문 히스토리 저장
        //포인트 사용 히스토리 저장
    }



}
