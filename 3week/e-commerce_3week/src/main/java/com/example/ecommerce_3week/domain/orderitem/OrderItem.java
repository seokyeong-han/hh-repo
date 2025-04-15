package com.example.ecommerce_3week.domain.orderitem;

import com.example.ecommerce_3week.domain.order.Order;

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
