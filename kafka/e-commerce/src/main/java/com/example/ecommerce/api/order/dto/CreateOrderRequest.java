package com.example.ecommerce.api.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    //private String orderId;
    private Long userId;
    private List<ProductOrderItem> items;

}
