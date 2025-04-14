package com.example.ecommerce_3week.dto.user;

import com.example.ecommerce_3week.domain.user.User;

public class UserResponse {
    //facade → controller
    private Long id;
    private String username;
    private Long balance;

    public UserResponse(UserFacadeResponse fac) {
        this.id = id;
        this.username = username;
        this.balance = balance != null ? balance : 0L;
    }
}
