package com.example.ecommerce_3week.dto.user;

public class UserFacadeResponse {
    private Long id;
    private String username;
    private Long balance;

    public UserFacadeResponse(Long id, String username, Long balance) {
        this.id = id;
        this.username = username;
        this.balance = balance != null ? balance : 0L;
    }
}
