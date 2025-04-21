package com.example.ecommerce.api.order.dto;

import lombok.Getter;

@Getter
public class OrderCommand {
    private Long productId;
    private int quantity;
    private final Long couponId;

    public OrderCommand(Long productId, int quantity, Long couponId) {
        this.productId = productId;
        this.quantity = quantity;
        this.couponId = couponId; //상품별 쿠폰 1개
    }

}
