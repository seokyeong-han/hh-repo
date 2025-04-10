package com.example.ecommerce_3week.dto.order.facade;

import com.example.ecommerce_3week.domain.order.OrderItem;
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
}
