package com.example.ecommerce_3week.infrastructure.user;

import org.springframework.data.jpa.repository.JpaRepository;

    public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
}
