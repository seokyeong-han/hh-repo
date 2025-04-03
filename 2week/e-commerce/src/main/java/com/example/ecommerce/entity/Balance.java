package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceId;

    private Long amount;

    private Date updateDt;

    @OneToOne
    @JoinColumn(name = "user_id") // 외래키 지정
    private User user;

    // ✅ 기본 생성자 (JPA에서 필요)
    public Balance() {}

    // ✅ 매개변수를 받는 생성자 추가
    public Balance(User user, Long amount) {
        this.user = user;
        this.amount = (amount != null) ? amount : 0L;
    }

    public Long getBalance() { //잔액 조회
        return this.amount;
    }
    public void setBalance(Long amount) {this.amount = amount;}
    //충전

    //사용


}
