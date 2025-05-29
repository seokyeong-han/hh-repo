package com.example.ecommerce.domain.user.model;

import com.example.ecommerce.domain.user.enetity.UserJpaEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private Long id;
    private String name;
    private Long balance;
    private Long version;//낙관락
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public User(Long id, String name, Long balance, Long version, LocalDateTime createdAt, LocalDateTime updateAt) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.version = version;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

    //잔액 차감
    public void deduct(Long amount) {
        if (amount == null || amount <= 0) throw new IllegalArgumentException("금액이 올바르지 않습니다.");
        if (this.balance < amount) throw new IllegalArgumentException("잔액이 부족합니다.");
        this.balance -= amount;
    }

    // 정적 팩토리 메서드 (JPA Entity → Domain)
    public static User toDomain(UserJpaEntity entity) {
        return new User(
          entity.getId()
          ,entity.getName()
          ,entity.getBalance()
          ,entity.getVersion()
          ,entity.getCreatedAt()
          ,entity.getUpdateAt()
        );

    }
}
