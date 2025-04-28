package com.example.ecommerce.domain.point.repository;

import com.example.ecommerce.domain.point.model.PointHistory;

public interface PointHistoryRepository {
    PointHistory save(PointHistory history);
}
