package com.example.ecommerce_3week.infrastructure.user;

import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(this::toDomain); //JPA Entity → 도메인 모델로 변환
    }

    private User toDomain(UserJpaEntity entity) {
        return new User(entity.getId(), entity.getUsername());
    }
}
