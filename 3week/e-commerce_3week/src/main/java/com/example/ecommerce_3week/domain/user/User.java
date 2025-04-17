package com.example.ecommerce_3week.domain.user;

import com.example.ecommerce_3week.infrastructure.user.UserJpaEntity;

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

    public User(String username, Long balance) { //id는 자동 생성자라 넣을 필요 없음
        this.username = username;
        this.balance = balance != null ? balance : 0L;
    }

    public void deduct(Long amount) {
        if (amount == null || amount <= 0) throw new IllegalArgumentException("금액이 올바르지 않습니다.");
        if (this.balance < amount) throw new IllegalArgumentException("잔액이 부족합니다.");
        this.balance -= amount;
    }

    public void charge(Long amount) {
        validateAmount(amount);
        this.balance = getBalance() + amount;
    }

    private void validateAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("충전금액은 0보다 커야합니다.");
        }
        if ((this.balance != null ? this.balance : 0L) + amount > 1_000_000L) {
            throw new IllegalArgumentException("최대금액 이상 충전 하실 수 없습니다.");
        }
    }

    public static User toDomain(UserJpaEntity entity) {
        return new User(entity.getId(), entity.getUsername(), entity.getBalance());
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
