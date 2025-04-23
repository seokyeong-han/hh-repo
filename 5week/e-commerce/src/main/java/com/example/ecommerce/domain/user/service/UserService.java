package com.example.ecommerce.domain.user.service;

import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor //@Autowired 안하고 private로 주입하려면 생성자가 필요한데 스프링 어노테이션으로 자동생성
public class UserService {
    private UserRepository userRepository;

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
