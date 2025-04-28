package com.example.ecommerce.api.point.dto;

import lombok.Getter;

@Getter //내부 DTO는 불변으로 관리
public class PointCommand {
    private Long userId;
    private Long amount;

    public PointCommand(Long userId, Long amount) { //기본생성자
        this.userId = userId;
        this.amount = amount;
    }

    public static PointCommand from(PointChargeRequest request) {
        return new PointCommand(request.getUserId(), request.getAmount());
    }

}
