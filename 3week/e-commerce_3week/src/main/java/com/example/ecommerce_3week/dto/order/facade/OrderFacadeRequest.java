package com.example.ecommerce_3week.dto.order.facade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderFacadeRequest {
    private Long productId;
    private int quantity;

    public OrderFacadeRequest(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
