package com.example.ecommerce.api.product.controller;

import com.example.ecommerce.api.product.dto.ProductResponse;
import com.example.ecommerce.api.product.facade.ProductFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("api/product")
@RequiredArgsConstructor
public class ProductController {
    private ProductFacade productFacade;

    //상품 조회
    @GetMapping({"/", "/{id}"})
    public ResponseEntity<List<ProductResponse>> getProduct(@PathVariable(required = false) Long id) {
        if (id == null) { //전체 조회
            List<ProductResponse> products = productFacade.getAllProducts();
            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();  // 204 상태 코드 반환
            }
            return ResponseEntity.ok(products);
        }else {//id 조회
            ProductResponse product = productFacade.getProductById(id);
            if (product == null) {
                return ResponseEntity.noContent().build();// 404 상태 코드
            }
            return ResponseEntity.ok(List.of(product)); // 단일 객체를 리스트에 감싸서 반환
        }
    }
}
