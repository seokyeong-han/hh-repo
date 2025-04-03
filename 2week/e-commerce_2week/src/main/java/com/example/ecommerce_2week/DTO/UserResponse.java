package com.example.ecommerce_2week.DTO;

import com.example.ecommerce_2week.entity.User;
import lombok.Getter;
@Getter
public class UserResponse {

    private Long userId;
    private String name;
    private Long balance;

    public UserResponse(User user){
        this.userId = user.getId();
        this.name = user.getName();
        this.balance = user.getBalance();

    }

}
