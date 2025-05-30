package com.example.ecommerce.infrastructure.user.jpaRepository;

import com.example.ecommerce.domain.user.enetity.UserJpaEntity;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepository;

    public JpaUserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findById(Long id){
        return jpaRepository.findById(id)
                .map(User::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity saved = jpaRepository.save(UserJpaEntity.fromDomain(user));
        return User.toDomain(saved);
    }

}
