package com.example.ecommerce.domain.order.model;

public class OrderItem {
    private Long orderId;
    private Long productId;
    private int quantity;

    private Long pricePerItem;       // 원래 개당 가격
    private Long totalOriginalPrice; // 할인 전 총 가격

    private Long discountAmount;     // 총 할인 금액
    private Long finalPricePerItem;  // 할인 적용 후 개당 가격
    private Long finalTotalPrice;    // 할인 적용 후 총 가격

    private Long couponId;           // 사용된 쿠폰

    public OrderItem(Long orderId,
                     Long productId,
                     int quantity,
                     Long pricePerItem,
                     Long totalOriginalPrice,
                     Long discountAmount,
                     Long finalPricePerItem,
                     Long finalTotalPrice,
                     Long couponId) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.totalOriginalPrice = totalOriginalPrice;
        this.discountAmount = discountAmount;
        this.finalPricePerItem = finalPricePerItem;
        this.finalTotalPrice = finalTotalPrice;
        this.couponId = couponId;
    }

    // couponId 없이도 만들 수 있는 생성자 (주문 전 단계 등)
    public OrderItem(Long productId,
                     int quantity,
                     Long pricePerItem,
                     Long totalOriginalPrice,
                     Long discountAmount,
                     Long finalPricePerItem,
                     Long finalTotalPrice,
                     Long couponId) {
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.totalOriginalPrice = totalOriginalPrice;
        this.discountAmount = discountAmount;
        this.finalPricePerItem = finalPricePerItem;
        this.finalTotalPrice = finalTotalPrice;
        this.couponId = couponId;
    }

    // Getter 들
    public Long getOrderId() {
        return orderId;
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
    public Long getTotalOriginalPrice() {
        return totalOriginalPrice;
    }
    public Long getDiscountAmount() {
        return discountAmount;
    }
    public Long getFinalPricePerItem() {
        return finalPricePerItem;
    }
    public Long getFinalTotalPrice() {
        return finalTotalPrice;
    }
    public Long getCouponId() {
        return couponId;
    }
}
