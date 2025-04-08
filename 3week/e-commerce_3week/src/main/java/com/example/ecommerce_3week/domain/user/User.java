package com.example.ecommerce_3week.domain.user;

import java.time.LocalDateTime;

public class User {
    //Domain Entity
    //어노테이션이 없어 특정 프레임워크에 의존하지 않고 어떤 환경에서도 재사용 가능

    private Long id;
    private String username;
    private Long balance;
    private LocalDateTime createdAt;

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
        this.createdAt = LocalDateTime.now(); // 생성 시점 기록
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
