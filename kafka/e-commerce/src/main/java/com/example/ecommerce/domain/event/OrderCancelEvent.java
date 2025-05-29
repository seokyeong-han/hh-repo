package com.example.ecommerce.domain.event;

public record OrderCancelEvent(
        String orderId,
        Long resultOrderId,
        Long userId
        //String reason // 선택: "결제 실패" 등의 이유를 담을 수 있음
) {

}
