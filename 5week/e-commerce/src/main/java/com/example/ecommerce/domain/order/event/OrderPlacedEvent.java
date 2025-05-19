package com.example.ecommerce.domain.order.event;

import java.util.List;

public class OrderPlacedEvent {
    private final List<Long> productIds;

    public OrderPlacedEvent(List<Long> productIds) {
        this.productIds = productIds;
    }

    public List<Long> getProductIds() {
        return productIds;
    }
}
