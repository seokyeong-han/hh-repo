package com.example.ecommerce.common.recode;

import java.util.List;

public record StockRollbackRequestedEvent(List<StockReserveRequest> items) {
}
