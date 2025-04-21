package com.example.ecommerce.domain.point.service;

import com.example.ecommerce.api.point.dto.PointCommand;
import com.example.ecommerce.domain.point.model.PointHistory;
import com.example.ecommerce.domain.point.repository.PointHistoryRepository;
import com.example.ecommerce.domain.point.repository.PointRepository;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.repository.UserRepository;
import com.example.ecommerce.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {
    private UserRepository userRepository;
    private PointRepository pointRepository;
    private PointHistoryRepository pointHistoryRepository;

    public void chargePoint (@RequestBody PointCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));
        //충전
        user.charge(command.getAmount());
        //포인트 충전 히스토리 저장
        PointHistory pointHistory = PointHistory.charge(command.getUserId(), command.getAmount());
        pointHistoryRepository.save(pointHistory);
    }

}
