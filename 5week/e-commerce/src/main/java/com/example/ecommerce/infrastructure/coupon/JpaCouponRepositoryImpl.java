package com.example.ecommerce.infrastructure.coupon;

import com.example.ecommerce.domain.coupon.entity.CouponJpaEntity;
import com.example.ecommerce.domain.coupon.model.Coupon;
import com.example.ecommerce.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCouponRepositoryImpl implements CouponRepository {
    private final CouponJpaRepository jpaRepository;

    @Override
    public List<Coupon> findAll() {
        return jpaRepository.findAll().stream()
                .map(CouponJpaEntity::toDomain)// JPA → 도메인
                .toList();
    }

    @Override
    public Optional<Coupon> findById(Long id) {
        return jpaRepository.findById(id)
                .map(CouponJpaEntity::toDomain);
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponJpaEntity entity = CouponJpaEntity.fromDomain(coupon);
        CouponJpaEntity saved = jpaRepository.save(entity); // Spring Data JPA
        return saved.toDomain();
    }

    //비관적 락 조회
    @Override
    public Optional<Coupon> findWithLockById(Long id) {
        return jpaRepository.findWithLockById(id)
                .map(CouponJpaEntity::toDomain);
    }



}
