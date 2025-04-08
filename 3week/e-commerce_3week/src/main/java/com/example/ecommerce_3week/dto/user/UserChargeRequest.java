package com.example.ecommerce_3week.dto.user;

import lombok.Getter;

@Getter
public class UserChargeRequest {
    private Long userId;
    private Long amount;
}
