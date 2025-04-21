package com.example.ecommerce.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Long userId;
    private List<OrderItemRequest> items;
    private Long orderCouponId; //전체 쿠폰 1개

    @Getter
    @Setter
    @AllArgsConstructor //생성자 추가
    public static class OrderItemRequest {
        private Long productId;
        private int quantity;
        private Long couponId; //상품별 쿠폰 1개
    }

    public static List<OrderCommand> toCommand(OrderRequest request) {
        return request.getItems().stream()
                .map(i -> new OrderCommand(i.getProductId(), i.getQuantity(), i.getCouponId()))
                .toList();
    }
}
