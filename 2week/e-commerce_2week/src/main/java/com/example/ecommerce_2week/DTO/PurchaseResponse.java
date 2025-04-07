package com.example.ecommerce_2week.DTO;

import com.example.ecommerce_2week.entity.Transaction;
import lombok.Getter;

@Getter
public class PurchaseResponse {
    private Long transactionId;
    private Long userId;
    private Long productId;
    private int quantity;
    private Long remainingBalance;

    // 생성자 추가
    public PurchaseResponse(Transaction transaction) {
        this.transactionId = transaction.getId();
        this.userId = transaction.getUser().getId();
        this.productId = transaction.getProduct().getId();
        this.quantity = transaction.getQuantity();
        this.remainingBalance = transaction.getUser().getBalance().getAmount();
    }
}
