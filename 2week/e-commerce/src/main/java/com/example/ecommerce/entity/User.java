package com.example.ecommerce.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Entity
public class User {
    private static final Long MAX_BALANCE = 100L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Balance balance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    public Long getBalance(){
        return balance != null ? balance.getAmount() : 0L;
    }

    //잔고 충전
    public void chargeBalance(Long amount){
        Long newBalance = balance.getAmount() + amount;
        if(newBalance > MAX_BALANCE){
            throw new IllegalArgumentException("최대 잔고를 초과할 수 없습니다.");
        }
        //잔고 업데이트
        this.balance.setAmount(newBalance);
    }
    //잔고 차감
    public void useBalance(Long amount){
        if(balance.getAmount() < 0){
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
    }


}
