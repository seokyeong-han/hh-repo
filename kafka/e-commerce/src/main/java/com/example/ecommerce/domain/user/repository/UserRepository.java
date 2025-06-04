package com.example.ecommerce.domain.user.repository;

import com.example.ecommerce.domain.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    long count();
    Optional<User> findById(Long id);
    User save(User user);
    void saveAll(List<User> users);
}
