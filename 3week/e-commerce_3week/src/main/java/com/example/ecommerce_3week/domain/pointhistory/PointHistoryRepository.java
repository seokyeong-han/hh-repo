package com.example.ecommerce_3week.domain.pointhistory;

import java.util.List;

public interface PointHistoryRepository {
    void save(PointHistory history);
    List<PointHistory> findByUserId(Long userId);

    void deleteAll();
}
