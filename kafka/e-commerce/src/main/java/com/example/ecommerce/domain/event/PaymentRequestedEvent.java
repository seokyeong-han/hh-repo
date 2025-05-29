package com.example.ecommerce.domain.event;

public record PaymentRequestedEvent(
        String orderId,
        Long userId,
        Long totalAmount) {
}
