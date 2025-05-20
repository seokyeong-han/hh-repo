package com.example.ecommerce.common.recode;

import java.util.List;

public record StockReservedEvent(
        Long userId,
        List<StockReservedItem> items
) {
}
