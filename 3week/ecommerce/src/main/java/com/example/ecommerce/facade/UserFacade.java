package com.example.ecommerce.facade;

import com.example.ecommerce.domain.user.User;
import com.example.ecommerce.dto.user.UserWithBalanceResponseDto;
import com.example.ecommerce.service.point.PointService;
import com.example.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor //lombok생성자 자동 생성
@Component
public class UserFacade {
    private final UserService userService;
    private final PointService pointService;

    public UserWithBalanceResponseDto getUserWithBalance(Long userId) {
        User user = userService.getUser(userId);           // 유저 조회
        Long balance = pointService.getBalance(userId);    // 포인트 조회

        return new UserWithBalanceResponseDto(
                user.getId(),
                user.getUsername(),
                balance
        );
    }
}
