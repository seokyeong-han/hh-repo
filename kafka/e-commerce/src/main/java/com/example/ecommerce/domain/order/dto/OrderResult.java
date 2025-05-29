package com.example.ecommerce.domain.order.dto;

public record OrderResult(
        Long orderId,
        Long totalPrice) {
}
