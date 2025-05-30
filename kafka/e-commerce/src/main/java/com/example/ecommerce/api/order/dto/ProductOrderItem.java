package com.example.ecommerce.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor 기본생성자 생성
public class ProductOrderItem {
    private Long productId;
    private int quantity;

    // ✅ 기본 생성자 (Jackson 역직렬화를 위해 필요)
    public ProductOrderItem() {
    }

    public ProductOrderItem(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
