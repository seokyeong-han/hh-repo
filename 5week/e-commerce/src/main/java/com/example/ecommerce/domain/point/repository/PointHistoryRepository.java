package com.example.ecommerce.domain.point.repository;

import com.example.ecommerce.domain.point.model.PointHistory;

public interface PointHistoryRepository {
    void save(PointHistory history);
}
