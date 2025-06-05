package com.example.ecommerce.api.ranking.dto;

import lombok.Getter;

@Getter
public class PopularProductDto {
    private Long productId;
    private Long viewCount;

    public PopularProductDto(Long productId, Long viewCount) {
        this.productId = productId;
        this.viewCount = viewCount;
    }
}
