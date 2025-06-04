package com.example.ecommerce.config;

import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer  implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        System.out.println("앱 시작 후 자동 실행됨!");

        //test user setting
        if (userRepository.count() == 0) {
            List<User> users = new ArrayList<>();
            for (int i = 1; i <= 500; i++) {
                users.add(new User(
                        null, "test-user-" + i, 10000L + i, null, LocalDateTime.now(), LocalDateTime.now()
                ));
            }
            userRepository.saveAll(users);
            System.out.println("✅ 500명 유저 저장 완료");
        }



    }
}
