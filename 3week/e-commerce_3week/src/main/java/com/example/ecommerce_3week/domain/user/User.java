package com.example.ecommerce_3week.domain.user;

import java.time.LocalDateTime;

public class User {
    //Domain Entity
    //어노테이션이 없어 특정 프레임워크에 의존하지 않고 어떤 환경에서도 재사용 가능

    private Long id;
    private String username;
    private Long balance;
    private LocalDateTime createdAt;

    public User(Long id, String username, Long balance) {
        this.id = id;
        this.username = username;
        this.balance = balance != null ? balance : 0L;
    }

    public void deduct(Long amount) {
        if (amount == null || amount <= 0) throw new IllegalArgumentException("금액이 올바르지 않습니다.");
        if (this.balance < amount) throw new IllegalArgumentException("잔액이 부족합니다.");
        this.balance -= amount;
    }

    public void charge(Long amount) {
        this.balance = getBalance() + amount;
    }
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Long getBalance() {
        return balance;
    }
}
