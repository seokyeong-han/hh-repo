package com.example.ecommerce_3week.facade;

import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.dto.user.UserChargeRequest;
import com.example.ecommerce_3week.dto.user.UserFacadeResponse;

import com.example.ecommerce_3week.service.pointhistory.PointHistoryService;
import com.example.ecommerce_3week.service.user.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;
    private final PointHistoryService pointHistoryService;

    //조회
    public UserFacadeResponse getUserById(Long id) {
        User user = userService.findUserById(id);
        return new UserFacadeResponse(user.getId(), user.getUsername(), user.getBalance());
    }


    //충전
    public void chargeUser(UserChargeRequest request){
        Long userId = request.getUserId();
        Long amount = request.getAmount();

        //유저 조회
        User user = userService.findUserById(userId);
        //충전 로직 (유효성 검사 + balance 변경)
        userService.charge(user, amount);
        //유저 저장
        User chargUser = userService.save(user);
        //히스토리 저장
        pointHistoryService.chargeSave(chargUser);

    }

}
