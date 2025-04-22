package com.example.ecommerce.domain.point.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.ecommerce.api.point.dto.PointCommand;
import com.example.ecommerce.domain.point.model.PointHistory;
import com.example.ecommerce.domain.point.repository.PointHistoryRepository;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.annotation.Backoff;

@Service
@RequiredArgsConstructor
public class PointService {

    private static final Logger log = LoggerFactory.getLogger(PointService.class);

    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Retryable( //낙관적 락
            value = { OptimisticLockingFailureException.class },
            maxAttempts = 10, //실패시 재시도 횟수
            backoff = @Backoff(delay = 100) // ms 단위
    )
    @Transactional
    public void chargePoint (@RequestBody PointCommand command) {
        // 재시도 되는지 확인용 로그
        // log.info("🔥 chargePoint called for userId={}, amount={}", command.getUserId(), command.getAmount());

        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));
        //충전
        user.charge(command.getAmount());
        //유저 저장
        User chargUser = userRepository.save(user);
        //포인트 충전 히스토리 저장
        PointHistory pointHistory = PointHistory.charge(chargUser.getId(), command.getAmount(), chargUser.getBalance());
        pointHistoryRepository.save(pointHistory);
    }

}
