package com.example.ecommerce.dto.user;

import lombok.Getter;

@Getter
public class UserWithBalanceResponseDto {
    private Long id;
    private String username;
    private Long balance; //잔액
}
