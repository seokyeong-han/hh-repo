package com.example.ecommerce_3week.service.user;

import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    //Service 계층은 도메인 로직을 구현하는 곳 이라 도메인 모델을 직접 써도 괜찮다..?
    private static final Long MAX_CHARGE_AMOUNT = 1_000_000L;

    //조회
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));
    }

    //충전
    public void charge(User user, Long amount) {
        user.charge(amount); // 실제 변경은 엔티티가 함
    }

    //저장
    public User save(User user){
        return userRepository.save(user);
    }

}
