package com.example.ecommerce.domain.order.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private Long userId;
    private List<OrderItem> items;
    private Long totalPrice;
    private LocalDateTime createdAt;

    // 도메인 복원용 생성자 (JPA Entity → 도메인 변환 시 사용)
    public Order(Long id, Long userId, Long totalPrice, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.items = List.of(); // 또는 빈 리스트로 초기화 (필요 시 아이템도 따로 세팅)
    }

    public Order(Long userId, List<OrderItem> items) {
        this.userId = userId;
        this.items = items;
        this.totalPrice = calculateTotalPrice(items);
        this.createdAt = LocalDateTime.now();
    }

    private Long calculateTotalPrice(List<OrderItem> items) {
        return items.stream()
                .mapToLong(OrderItem::getTotalPrice)
                .sum();
    }

    //Getter
    public Long getId() {return id;}
    public Long getUserId() {return userId;}
    public List<OrderItem> getItems() {return items;}
    public Long getTotalPrice() {return totalPrice;}
    public LocalDateTime getCreatedAt() {return createdAt;}

}
