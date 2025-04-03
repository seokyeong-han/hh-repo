package com.example.ecommerce.DTO;

import com.example.ecommerce.entity.Coupon;
import com.example.ecommerce.entity.User;
import lombok.Getter;

@Getter
public class UserResponse {
    private Long userId;
    private String name;
    private Long balance;

    public UserResponse(User user){
        this.userId = user.getUserId();
        this.name = user.getName();
        this.balance = user.getBalance();

    }
}
