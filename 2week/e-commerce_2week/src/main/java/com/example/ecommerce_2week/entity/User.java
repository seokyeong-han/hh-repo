package com.example.ecommerce_2week.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    private static final Long MAX_BALANCE = 1_000_000L; //최대 100만
    private static final Long MIN_BALANCE = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Balance balance;

    public Long getBalanceEntity(){
        return balance;
    }

    //충전 메서드
    public void chargeBalance(Long amount) {
        if (balance.getAmount() + amount > MAX_BALANCE) {
            throw new IllegalArgumentException("최대 잔고를 초과하였습니다.");
        }
        balance.setAmount(balance.getAmount() + amount);
    }
}
