package com.example.ecommerce.api.ranking.controller;

import com.example.ecommerce.api.ranking.dto.PopularProductDto;
import com.example.ecommerce.api.ranking.facade.PopularProductFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/popularProduct")
@RequiredArgsConstructor
public class PopularProductController {
    private final PopularProductFacade popularProductFacade;

    @GetMapping("/today")
    public ResponseEntity<List<PopularProductDto>> getTodayTopPopularProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<PopularProductDto> todayProducts = popularProductFacade.getTodayTopPopularProductIds(limit);
        return ResponseEntity.ok(todayProducts);
    }
}
