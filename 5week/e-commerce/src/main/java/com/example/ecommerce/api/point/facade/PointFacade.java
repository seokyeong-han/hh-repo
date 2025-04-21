package com.example.ecommerce.api.point.facade;

import com.example.ecommerce.api.point.dto.PointChargeRequest;
import com.example.ecommerce.api.point.dto.PointCommand;
import com.example.ecommerce.domain.point.service.PointService;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@RequiredArgsConstructor
public class PointFacade {
    private UserService userService;
    private PointService pointService;

    //포인트 충전
    public void chargePoint (@RequestBody PointChargeRequest request) {
        //유저조회
        User user = userService.findById(request.getUserId());
        //포인트충전, 포인트 히스토리 저장
        pointService.chargePoint(PointCommand.from(request));
    }


}
