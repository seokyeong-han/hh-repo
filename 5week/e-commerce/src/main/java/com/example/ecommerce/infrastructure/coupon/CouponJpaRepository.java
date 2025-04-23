package com.example.ecommerce.infrastructure.coupon;

import com.example.ecommerce.domain.coupon.entity.CouponJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<CouponJpaEntity,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CouponJpaEntity c WHERE c.id = :id")
    Optional<CouponJpaEntity> findWithLockById(@Param("id") Long id);

}
