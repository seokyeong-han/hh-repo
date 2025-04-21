package com.example.ecommerce.infrastructure.user;

import com.example.ecommerce.domain.user.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity,Long> {
}
