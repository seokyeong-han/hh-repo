package com.example.ecommerce.domain.payment.service;

import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final UserRepository userRepository;

    @Transactional
    public void paymentProcessor(Long userId, Long totalAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(" 존재하지 않는 유저입니다. userId={}", userId);
                    return new IllegalArgumentException("존재하지 않는 유저입니다.");
                });
        user.deduct(totalAmount); // 도메인 로직: 잔액 차감 및 부족 시 예외
        userRepository.save(user); // @Version에 의해 낙관적 락 적용됨
    }
}
