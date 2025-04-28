package com.example.ecommerce.domain.user.entity;

import com.example.ecommerce.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user")
public class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private Long balance;

    @Version
    private Long version; //낙관적 락 필드

    //기본 생성자 (JPA 필수)
    protected UserJpaEntity() {}

    //전체 필드 초기화용 생성자
    public UserJpaEntity(Long id, String username, Long balance, Long version) {
        this.id = id;
        this.username = username;
        this.balance = balance;
        this.version = version;
    }

    // 도메인 → JPA
    public static UserJpaEntity fromDomain(User user) {
        return new UserJpaEntity(
                user.getId(),
                user.getUsername(),
                user.getBalance(),
                user.getVersion()
        );
    }

    // JPA → 도메인
    public User toDomain() {
        return new User(id, username, balance, version);
    }
}

