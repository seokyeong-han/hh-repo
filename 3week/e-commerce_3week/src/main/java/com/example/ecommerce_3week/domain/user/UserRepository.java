package com.example.ecommerce_3week.domain.user;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    //Domain
    //도메인 관점에서 유저를 저장하고 조회하는 기능의 정의만 함 (DB, Redis 등과 무관)
    Optional<User> findById(Long id);
    User save(User user);

    void deleteAll();

    List<User> findAll();
}
