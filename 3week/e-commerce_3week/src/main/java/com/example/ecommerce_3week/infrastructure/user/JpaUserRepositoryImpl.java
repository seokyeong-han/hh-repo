package com.example.ecommerce_3week.infrastructure.user;

import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaUserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
                .map(User::toDomain); //JPA Entity → 도메인 모델로 변환
    }
    @Override
    public User save(User user) {
        return User.toDomain(
                jpaRepository.save(toEntity(user))
        );
    }
    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .map(User::toDomain)
                .collect(Collectors.toList());
    }



    // 도메인 → JPA Entity 변환
    private UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(user.getId(), user.getUsername(), user.getBalance());
    }
}
