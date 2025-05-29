package com.example.ecommerce.domain.order.entity.orderItem;

import com.example.ecommerce.domain.order.model.orderItem.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "order_item")
public class OrderItemJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId; // 외래키지만 연관관계 X
    private Long productId;
    private int quantity;
    private Long pricePerItem;
    private Long totalPrice;
    private LocalDateTime createdAt;

    public OrderItemJpaEntity() {

    }

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
}
