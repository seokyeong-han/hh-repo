package com.example.ecommerce.api.point.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class pointChargeRequest {
    private Long userId;
    private Long amount;

}
