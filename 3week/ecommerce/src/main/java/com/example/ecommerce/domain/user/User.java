package com.example.ecommerce.domain.user;

public class User {
    //Domain Entity
    //어노테이션이 없어 특정 프레임워크에 의존하지 않고 어떤 환경에서도 재사용 가능

    private Long id;
    private String username;

    public User (Long id, String username) {
        this.id = id;
        this.username = username;

    }
}
