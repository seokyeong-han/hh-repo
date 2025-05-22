package com.example.ecommerce.common.recode;

import java.util.List;

public record StockReserveRequestedEvent(
        Long userId,
        List<StockReserveRequest> items
) {
}
