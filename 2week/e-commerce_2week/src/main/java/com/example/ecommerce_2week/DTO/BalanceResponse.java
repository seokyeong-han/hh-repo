package com.example.ecommerce_2week.DTO;

import com.example.ecommerce_2week.entity.Balance;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BalanceResponse {

    private Long id;
    private Long amount;
    private LocalDateTime updateDt;

    public BalanceResponse(Balance balance) {
        this.id = balance.getId();
        this.amount = balance.getAmount();
        this.updateDt = balance.getUpdateDt();
    }
}

