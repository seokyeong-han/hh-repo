package com.example.ecommerce.entity;

import com.example.ecommerce.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Enumerated(EnumType.STRING)  // Enum을 String으로 저장
    private TransactionType transactionType;

    private Date updateDt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
}
