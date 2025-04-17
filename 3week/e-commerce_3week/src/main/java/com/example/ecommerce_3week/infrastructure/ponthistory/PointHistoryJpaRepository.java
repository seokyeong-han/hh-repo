package com.example.ecommerce_3week.infrastructure.ponthistory;

import com.example.ecommerce_3week.domain.pointhistory.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistoryJpaEntity, Long> {
    List<PointHistoryJpaEntity> findAllByUserId(Long userId);
}
