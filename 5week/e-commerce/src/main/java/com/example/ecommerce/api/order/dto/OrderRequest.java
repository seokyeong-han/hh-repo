package com.example.ecommerce.api.order.dto;

import com.example.ecommerce.common.recode.StockReserveRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Long userId;
    private List<OrderItemRequest> items;

    @Builder
    public OrderRequest(Long userId, List<OrderItemRequest> items) {
        this.userId = userId;
        this.items = items;
    }

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
