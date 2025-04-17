package com.example.ecommerce_3week.domain.orderitem;

import com.example.ecommerce_3week.domain.order.Order;
import com.example.ecommerce_3week.infrastructure.orderitem.OrderItemJpaEntity;

public class OrderItem {
    private Long productId;
    private int quantity;
    private Long pricePerItem; //상품 한개당 가격
    private Order order; // 연관관계 추가

    public OrderItem(Long productId, int quantity, Long pricePerItem) {
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
    }

    public OrderItem(Long productId, int quantity, Long pricePerItem, Order order) {
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.order = order;
    }

    public static OrderItem toDomain(OrderItemJpaEntity entity) {
        Order order = Order.toDomain(entity.getOrder()); // ← 이 메서드 필요
        return new OrderItem(
                entity.getProductId(),
                entity.getQuantity(),
                entity.getPricePerItem(),
                order // ← 여기에 넣기
        );
    }


    public void assignOrder(Order order) {
        this.order = order;
    }

    public Long getTotalPrice() {
        return pricePerItem * quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Long getPricePerItem() {
        return pricePerItem;
    }


}
