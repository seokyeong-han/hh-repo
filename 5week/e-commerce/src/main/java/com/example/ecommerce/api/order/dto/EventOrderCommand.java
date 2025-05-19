package com.example.ecommerce.api.order.dto;

import lombok.Getter;

@Getter
public class EventOrderCommand {
    private Long productId;
    private int quantity;
    private long pricePerItem;

    public EventOrderCommand(Long productId, int quantity,  long pricePerItem) {
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
    }

}
