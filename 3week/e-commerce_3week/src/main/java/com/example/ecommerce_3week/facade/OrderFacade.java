package com.example.ecommerce_3week.facade;

import com.example.ecommerce_3week.common.enums.PointTransactionType;
import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.pointhistory.PointHistory;
import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.dto.order.controller.OrderRequest;

import com.example.ecommerce_3week.dto.order.facade.OrderFacadeRequest;
import com.example.ecommerce_3week.dto.order.facade.PreparedOrderItems;
import com.example.ecommerce_3week.service.order.OrderService;
import com.example.ecommerce_3week.service.orderhistory.OrderHistoryService;
import com.example.ecommerce_3week.service.pointhistory.PointHistoryService;
import com.example.ecommerce_3week.service.product.ProductService;
import com.example.ecommerce_3week.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final OrderHistoryService orderHistoryService;
    private final PointHistoryService pointHistoryService;

    @Transactional
    public void placeOrder(OrderRequest request){
        //유저 조회
        User user = userService.findUserById(request.getUserId());

        //파사드 dto로 변환
        List<OrderFacadeRequest> requestItems = OrderRequest.from(request);

        //상품조회,재고차감,재고저장
        PreparedOrderItems prepared = productService.prepareOrderItems(requestItems);

        //유저 잔액 차감, 저장
        userService.deductBalance(user, prepared.getTotalPrice()); //아이디랑 총액 넘기기, 여기서 유저 조회 합치기
        //User user = 리턴하기
        //주문생성, 주문 저장
        Order order = orderService.createOrder(user, prepared.getOrderItems());

        //주문 히스토리 저장
        orderHistoryService.save(user.getId(), order.getId(), prepared.getOrderItems());

        //포인트 히스토리 저장
        pointHistoryService.useSave(new PointHistory(user.getId(), prepared.getTotalPrice(), PointTransactionType.USE));

    }
}
