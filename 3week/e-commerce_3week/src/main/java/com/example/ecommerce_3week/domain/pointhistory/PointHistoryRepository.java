package com.example.ecommerce_3week.domain.pointhistory;

import java.util.List;

public interface PointHistoryRepository {
    void save(PointHistory history);
    List<PointHistory> findAllByUserId(Long userId);
}
