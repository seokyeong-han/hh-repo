package com.example.ecommerce.common.recode;

public record StockReserveRequest(//재고차감요청
        Long productId,
        int quantity
) {
    //MSA에서는 서비스 간 주고받는 DTO는 무조건 별도 공통 모듈에 정의하고,
    // 서비스마다 import만 해서 사용
}
