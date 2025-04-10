package com.example.ecommerce_3week.infrastructure.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@RequiredArgsConstructor //기본 생성자 주입 lombok
public class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private Long balance;

    public UserJpaEntity(Long id, String username, Long balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }
}
