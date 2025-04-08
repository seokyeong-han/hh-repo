package com.example.ecommerce.infrastructure.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserJpaEntity, Long> {

}
