package com.example.ecommerce_3week.domain.orderitem;

public class OrderItem {
    private Long orderId;
    private Long productId;
    private int quantity;
    private Long pricePerItem; //상품 한개당 가격

    public OrderItem(Long orderId, Long productId, int quantity, Long pricePerItem) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
    }

    public OrderItem withOrderId(Long orderId) {
        return new OrderItem(orderId, this.productId, this.quantity, this.pricePerItem);
    }

    public Long getTotalPrice() {
        return pricePerItem * quantity;
    }

    public long getOrderId() {return orderId;}

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
