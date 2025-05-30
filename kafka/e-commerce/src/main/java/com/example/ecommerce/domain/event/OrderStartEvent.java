package com.example.ecommerce.domain.event;

import com.example.ecommerce.api.order.dto.CreateOrderRequest;
import com.example.ecommerce.api.order.dto.ProductOrderItem;

import java.util.List;

public record OrderStartEvent (
        String orderId,
        Long userId,
        List<ProductOrderItem> items
){
}
