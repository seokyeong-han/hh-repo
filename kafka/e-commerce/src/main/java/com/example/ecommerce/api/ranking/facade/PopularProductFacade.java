package com.example.ecommerce.api.ranking.facade;

import com.example.ecommerce.api.ranking.dto.PopularProductDto;
import com.example.ecommerce.domain.ranking.PopularProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PopularProductFacade {
    private final PopularProductService popularProductService;

    public List<PopularProductDto>  getTodayTopPopularProductIds(int limit) {
        return popularProductService.getTodayTopPopularProductIds(limit);
    }
}
