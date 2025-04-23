package com.example.ecommerce.infrastructure.coupon;

import com.example.ecommerce.domain.coupon.entity.UserCouponJpaEntity;
import com.example.ecommerce.domain.coupon.model.UserCoupon;
import com.example.ecommerce.domain.coupon.repository.UserCouponRepository;
import com.example.ecommerce.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserCouponRepositoryImpl implements UserCouponRepository {
    private final UserCouponJpaRepository jpaRepository;

    @Override
    public Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId) {
        return jpaRepository.findByUserIdAndCouponId(userId, couponId)
                .map(UserCouponJpaEntity::toDomain);
    }

    @Override
    public List<UserCoupon> findAll() {
        return jpaRepository.findAll().stream()
                .map(UserCouponJpaEntity::toDomain)
                .toList();
    }

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        UserCouponJpaEntity saved = jpaRepository.save(UserCouponJpaEntity.fromDomain(userCoupon));
        return saved.toDomain();
    }
}
