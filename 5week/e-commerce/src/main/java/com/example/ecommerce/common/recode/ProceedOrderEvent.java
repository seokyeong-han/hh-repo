package com.example.ecommerce.common.recode;

import java.util.List;

public record ProceedOrderEvent(
        Long userId,
        List<StockReservedItem> items
) {
}
