package com.example.ecommerce.domain.order.entity;

import com.example.ecommerce.domain.order.model.OrderItem;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
public class OrderItemJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long productId;
    private int quantity;
    private Long totalPrice;
    private Long pricePerItem;
    private LocalDateTime createdAt;

    protected OrderItemJpaEntity() {}

    public OrderItemJpaEntity(Long id, Long orderId, Long productId, int quantity, Long totalPrice, Long pricePerItem, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.pricePerItem = pricePerItem;
        this.createdAt = createdAt;
    }

    public static OrderItemJpaEntity fromDomain(OrderItem item, Long orderId) {
        return new OrderItemJpaEntity(
                item.getId(),
                orderId,
                item.getProductId(),
                item.getQuantity(),
                item.getTotalPrice(),
                item.getPricePerItem(),
                item.getCreatedAt()
        );
    }

    public OrderItem toDomain() {
        return new OrderItem(id, orderId, productId, quantity, totalPrice, pricePerItem, createdAt);
    }

}
