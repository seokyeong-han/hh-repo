package com.example.ecommerce.infrastructure.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "users")
@Getter
public class UserJpaEntity {
    @Id @GeneratedValue
    private Long id;

    private String username;
}
