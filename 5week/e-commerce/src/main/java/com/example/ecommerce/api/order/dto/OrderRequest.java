package com.example.ecommerce.api.order.dto;

import lombok.AllArgsConstructor;
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
    @AllArgsConstructor //생성자 추가
    public static class OrderItemRequest {
        private Long productId;
        private int quantity;
    }

    public static List<OrderCommand> toCommand(OrderRequest request) {
        return request.getItems().stream()
                .map(i -> new OrderCommand(i.getProductId(), i.getQuantity()))
                .toList();
    }
}
