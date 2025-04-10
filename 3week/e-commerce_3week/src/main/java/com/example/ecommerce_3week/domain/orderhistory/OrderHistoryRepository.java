package com.example.ecommerce_3week.domain.orderhistory;

import java.util.List;

public interface OrderHistoryRepository {
    void saveAll(List<OrderHistory> histories);
}
