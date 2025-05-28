package com.example.ecommerce.api.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderEvent {
    private Long userId;
    private Long productId;
    private Integer quantity;
}
