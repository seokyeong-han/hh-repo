package com.example.ecommerce_3week.dto.order.facade;

import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import com.example.ecommerce_3week.domain.product.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PreparedOrderItems {
    private final List<Product> products;
    private final List<OrderItem> orderItems;

    public PreparedOrderItems(List<Product> products, List<OrderItem> orderItems) {
        this.products = products;
        this.orderItems = orderItems;
    }

    // 주문 항목들의 총액을 계산하는 메서드
    public Long getTotalPrice() {
        return orderItems.stream()
                .mapToLong(orderItem -> orderItem.getTotalPrice()) // 각 OrderItem의 총액 합산
                .sum();
    }
}
