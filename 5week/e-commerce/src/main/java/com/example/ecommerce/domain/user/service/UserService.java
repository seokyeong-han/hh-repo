package com.example.ecommerce.domain.user.service;

import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
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

}
