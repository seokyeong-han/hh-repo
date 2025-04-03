package com.example.ecommerce_2week.repository;

import com.example.ecommerce_2week.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
