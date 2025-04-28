package com.example.ecommerce.domain.order.model;
import java.time.LocalDateTime;

public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private Long totalPrice;
    private Long pricePerItem;       // 원래 개당 가격
    LocalDateTime createdAt;

    //기본 생성자
    public OrderItem(Long id, Long orderId, Long productId, int quantity, Long totalPrice, Long pricePerItem, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.pricePerItem = pricePerItem;
        //this.createdAt = LocalDateTime.now();
        this.createdAt = createdAt;
    }


    // Getter 들
    public Long getId() {return id;}
    public Long getOrderId() {return orderId;}
    public Long getProductId() {return productId;}
    public int getQuantity() {return quantity;}
    public Long getTotalPrice() {return totalPrice;}
    public Long getPricePerItem() {return pricePerItem;}
    public LocalDateTime getCreatedAt() {return createdAt;}

}
