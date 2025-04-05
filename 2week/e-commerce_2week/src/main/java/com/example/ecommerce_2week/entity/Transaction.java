package com.example.ecommerce_2week.entity;

import com.example.ecommerce_2week.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 거래 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 거래한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // 구매한 상품 (null 가능)

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon; // 사용한 쿠폰 (null 가능)
*/
    @Column(nullable = false)
    private Long amount; // 거래 금액

    @Column(nullable = false)
    private int quantity; //구매수량

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType transactionType; // 거래 유형 (USE, CHARGE)

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate = new Date(); // 거래 발생일 (자동 입력)

    //잔액사용
    public Transaction(User user, Product product, int quantity, Long totalPrice) {
        this.user = user;
        this.product = product;
        this.amount = Long.valueOf(totalPrice);
        this.quantity = quantity;
        this.quantity = quantity;
        this.transactionType = TransactionType.USE;
        this.transactionDate = new Date();
    }

    //잔액사용은 구현 못함
}
