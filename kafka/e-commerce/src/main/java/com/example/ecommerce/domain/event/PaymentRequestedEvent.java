package com.example.ecommerce.domain.event;

import com.example.ecommerce.domain.product.dto.ProductOrderItemMessage;

import java.util.List;

public record PaymentRequestedEvent(
        String orderId,
        Long userId,
        Long resultOrderId,
        Long totalAmount,
        List<ProductOrderItemMessage> items) {
}
