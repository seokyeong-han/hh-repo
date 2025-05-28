package com.example.ecommerce.domain.event;

import com.example.ecommerce.api.order.dto.ProductOrderItem;

import java.util.List;

public record StockSuccessEvent(
        String orderId,
        Long userId,
        List<ProductOrderItem> items
) {
}
