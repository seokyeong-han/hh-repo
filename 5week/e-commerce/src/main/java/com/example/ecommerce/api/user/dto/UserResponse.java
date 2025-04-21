package com.example.ecommerce.api.user.dto;

import com.example.ecommerce.domain.user.model.User;

public class UserResponse {
    private Long id;
    private String username;
    private Long balance;

    //생성자
    public UserResponse(Long id, String username, Long balance) {
        this.id = id;
        this.username = username;
        this.balance = balance != null ? balance : 0L;
    }

    //정적 팩토리 메서드
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getBalance());
    }
}
