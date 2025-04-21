package com.example.ecommerce.domain.user.repository;

import com.example.ecommerce.domain.user.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
}
