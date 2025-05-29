package com.example.ecommerce.domain.event;

import com.example.ecommerce.domain.product.dto.ProductOrderItemMessage;

import java.util.List;

public record StockSuccessEvent(
        String orderId,
        Long userId,
        List<ProductOrderItemMessage> items
) {
}
