package com.example.ecommerce_3week.dto.order.controller;

import com.example.ecommerce_3week.dto.order.facade.OrderFacadeRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Long userId;
    private List<OrderItemRequest> items;

    @Getter
    @Setter
    public static class OrderItemRequest {
        private Long productId;
        private int quantity;
    }

    public static List<OrderFacadeRequest> from(OrderRequest request) {
        return request.getItems().stream()
                .map(i -> new OrderFacadeRequest(i.getProductId(), i.getQuantity()))
                .toList();
    }
}
