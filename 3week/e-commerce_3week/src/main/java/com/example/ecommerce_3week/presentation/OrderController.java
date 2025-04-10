package com.example.ecommerce_3week.presentation;

import com.example.ecommerce_3week.dto.order.controller.OrderRequest;
import com.example.ecommerce_3week.facade.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderFacade orderFacade;

    /*
        요청 정보 받기 (userId, productId, 수량 등)
        유저 조회 (유효한 유저인지 확인)
        상품 조회 및 재고 확인
        상품 가격 * 수량 계산 → 총 주문 금액
        유저 포인트 또는 잔액 차감 (충분한지 확인)
        상품 재고 감소
        주문 정보 저장 (나중에 Order 테이블 생긴다면)
        히스토리 저장 (PointHistory, ProductHistory 등)
     */
    @PostMapping
    public ResponseEntity<Void> placeOrder(@RequestBody OrderRequest request) {
        orderFacade.placeOrder(request);
        return ResponseEntity.ok().build();
    }


}
