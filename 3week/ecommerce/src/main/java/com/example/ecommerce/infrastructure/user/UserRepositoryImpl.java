package com.example.ecommerce.infrastructure.user;

import com.example.ecommerce.domain.user.User;
import com.example.ecommerce.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor //lombok생성자 자동 생성
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;


    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id)
                .map(UserMapper::toDomain);
    }
}
