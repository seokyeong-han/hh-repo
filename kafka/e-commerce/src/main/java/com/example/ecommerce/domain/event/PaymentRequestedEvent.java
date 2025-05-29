package com.example.ecommerce.domain.event;

public record PaymentRequestedEvent(
        Long orderId,
        Long userId,
        Long totalAmount) {
}
