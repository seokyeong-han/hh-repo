package com.example.ecommerce.infrastructure.point;

import com.example.ecommerce.domain.point.entity.PointHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistoryJpaEntity,Long> {
}
