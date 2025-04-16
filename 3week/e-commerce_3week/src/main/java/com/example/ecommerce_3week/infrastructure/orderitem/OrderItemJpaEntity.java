package com.example.ecommerce_3week.infrastructure.orderitem;

import com.example.ecommerce_3week.infrastructure.order.OrderJpaEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString // Lombok의 @ToString
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderItemJpaEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderJpaEntity order;

    private Long productId;
    private int quantity;
    private Long pricePerItem;
    private Long totalPrice;

    public OrderItemJpaEntity(OrderJpaEntity order, Long productId, int quantity, Long pricePerItem) {
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
    }

}
