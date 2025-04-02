package com.example.ecommerce.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "\"user\"")  // 큰따옴표 사용
public class User {
    private static final Long MAX_BALANCE = 10000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Balance balance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    // ✅ 매개변수 있는 생성자 추가
    public User(Long userId, String namesS) {
        this.userId = userId;
        this.name = name;
    }

    public User() {} // JPA 기본 생성자 필요

    public Long getBalance(){
        return balance != null ? balance.getBalance() : 0L;
    }

    public void setBalance(Balance balance) { // ✅ setBalance() 메서드 추가
        this.balance = balance;
    }

    //잔고 충전
    public void chargeBalance(Long amount){
        Long newBalance = balance.getBalance() + amount;
        if(newBalance > MAX_BALANCE){
            throw new IllegalArgumentException("최대 잔고를 초과할 수 없습니다.");
        }
        //잔고 업데이트
        this.balance.setBalance(newBalance);
    }
    //잔고 차감
    public void useBalance(Long amount){
        if(balance.getBalance() < 0){
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
    }


}
