package com.example.ecommerce.domain.user.enetity;

import com.example.ecommerce.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@RequiredArgsConstructor
@Table(name = "users")
public class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long balance;
    @Version
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    //기본 생성자 (JPA 필수)
    public UserJpaEntity(Long id, String name, Long balance, Long version, LocalDateTime createdAt, LocalDateTime updateAt) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.version = version;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

    public static UserJpaEntity fromDomain(User domain) {
        return new UserJpaEntity(
                domain.getId()
                ,domain.getName()
                ,domain.getBalance()
                ,domain.getVersion()
                ,domain.getCreatedAt()
                ,domain.getUpdateAt()
        );
    }
}
