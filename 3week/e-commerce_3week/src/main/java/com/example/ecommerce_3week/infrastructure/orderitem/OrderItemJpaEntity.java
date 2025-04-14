package com.example.ecommerce_3week.infrastructure.orderitem;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderItemJpaEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private Long pricePerItem;
    private Long totalPrice;

    public OrderItemJpaEntity(Long orderId, Long productId, int quantity, Long pricePerItem) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.totalPrice = pricePerItem * quantity;
    }

}
