package com.example.ecommerce.api.point.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointChargeRequest {
    private Long userId;
    private Long amount;

}
