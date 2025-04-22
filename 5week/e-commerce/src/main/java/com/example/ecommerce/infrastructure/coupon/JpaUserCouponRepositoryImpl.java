package com.example.ecommerce.infrastructure.coupon;

import com.example.ecommerce.domain.coupon.entity.UserCouponJpaEntity;
import com.example.ecommerce.domain.coupon.model.UserCoupon;
import com.example.ecommerce.domain.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public void save(UserCoupon userCoupon) {
        UserCouponJpaEntity entity = UserCouponJpaEntity.fromDomain(userCoupon);
        jpaRepository.save(entity);
    }
}
