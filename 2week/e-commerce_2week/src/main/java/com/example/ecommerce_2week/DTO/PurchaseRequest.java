package com.example.ecommerce_2week.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseRequest {
    private Long userId;
    private Long productId;
    private int quantity; //구매 수량

    public PurchaseRequest(Long userId, Long productId, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
