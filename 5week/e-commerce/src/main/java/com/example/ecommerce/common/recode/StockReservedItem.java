package com.example.ecommerce.common.recode;

import com.example.ecommerce.domain.order.model.OrderItem;

import java.time.LocalDateTime;

public record StockReservedItem(
                                 Long productId,
                                 int quantity,
                                 long pricePerItem,
                                 long totalPrice
) {
    public OrderItem toOrderItem() {
        return new OrderItem(
                null, null, productId, quantity,
                totalPrice, pricePerItem, LocalDateTime.now()
        );
    }
}
