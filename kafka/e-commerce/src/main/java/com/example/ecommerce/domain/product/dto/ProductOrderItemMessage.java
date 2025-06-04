package com.example.ecommerce.domain.product.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderItemMessage {
    private Long productId;
    private int quantity;
    private long pricePerItem;   // 단가
    private long totalPrice;     // 총액

}
