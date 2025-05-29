package com.example.ecommerce.infrastructure.user.jpaRepository;

import com.example.ecommerce.domain.user.enetity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
}
