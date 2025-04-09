package com.example.ecommerce_3week.domain.order;

public class OrderItem {
    private Long productId;
    private int quantity;
    private Long pricePerItem; //한개당 가격

    public OrderItem(Long productId, int quantity, Long pricePerItem) {
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
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
