package com.example.ecommerce_3week.facade;

import com.example.ecommerce_3week.common.enums.PointTransactionType;
import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.domain.order.OrderItem;
import com.example.ecommerce_3week.domain.pointhistory.PointHistory;
import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.dto.order.controller.OrderRequest;

import com.example.ecommerce_3week.dto.order.facade.OrderFacadeRequest;
import com.example.ecommerce_3week.dto.order.facade.PreparedOrderItems;
import com.example.ecommerce_3week.service.order.OrderService;
import com.example.ecommerce_3week.service.orderhistory.OrderHistoryService;
import com.example.ecommerce_3week.service.pointhistory.PointHistoryService;
import com.example.ecommerce_3week.service.product.ProductService;
import com.example.ecommerce_3week.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final OrderHistoryService orderHistoryService;
    private final PointHistoryService pointHistoryService;

    public void placeOrder(OrderRequest request){
        //유저 조회
        User user = userService.findUserById(request.getUserId());

        //파사드 dto로 변환
        //피드백 수정
        List<OrderFacadeRequest> requestItems = OrderRequest.from(request);

        //상품조회 및 재고차감
        PreparedOrderItems prepared = productService.prepareOrderItems(requestItems);

        //주문생성
        Order order = orderService.createOrder(user, prepared.getOrderItems());
        //주문저장
        orderService.save(order);
        //유저저장
        userService.save(user);
        //재고감소 -> 변경된 상품 저장
        productService.save(prepared.getProducts());
        //주문 히스토리 저장
        orderHistoryService.save(user, prepared.getOrderItems());
        //포인트 히스토리 저장
        PointHistory pointHistory = new PointHistory(user.getId(), order.getTotalPrice(), PointTransactionType.USE);
        pointHistoryService.useSave(pointHistory);

    }
}
