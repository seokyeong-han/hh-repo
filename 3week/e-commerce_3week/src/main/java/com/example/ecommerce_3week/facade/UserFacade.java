package com.example.ecommerce_3week.facade;

import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.dto.user.UserChargeRequest;
import com.example.ecommerce_3week.dto.user.UserChargeResponse;
import com.example.ecommerce_3week.dto.user.UserFacadeResponse;

import com.example.ecommerce_3week.dto.user.UserResponse;
import com.example.ecommerce_3week.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;
    //조회
    public UserFacadeResponse getUserById(Long id) {
        User user = userService.findUserById(id);
        return new UserFacadeResponse(user.getId(), user.getUsername(), user.getBalance());
    }
    //도메인 반환?

    //충전
    public void chargeUser(UserChargeRequest request){
        Long userId = request.getUserId();
        Long amount = request.getAmount();

        //유저 조회
        User user = userService.findUserById(userId);
        //유효성 검증 및 balance 연산
        UserChargeResponse res = UseruserService.chargeUserBalance(user, amount);
        //유저 저장

        //히스토리 저장
    }

}
