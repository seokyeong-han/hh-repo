package com.example.ecommerce.domain.user.service;

import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));
    }

    @Transactional //잔액차감 낙관적 락 고려
    public User deductBalance(Long userId, Long amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        user.deduct(amount); // 도메인 내부에서 처리
        return userRepository.save(user); // 변경 감지 or save로 반영
    }

}
