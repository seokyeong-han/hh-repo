package com.example.ecommerce.api.order.dto;

import lombok.Getter;

@Getter
public class OrderCommand {
    private Long productId;
    private int quantity;

    public OrderCommand(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;

    }

}
