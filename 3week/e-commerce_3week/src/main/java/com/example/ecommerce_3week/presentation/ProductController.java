package com.example.ecommerce_3week.presentation;

import com.example.ecommerce_3week.dto.product.contorller.ProductResponse;
import com.example.ecommerce_3week.dto.product.contorller.ProductRequest;
import com.example.ecommerce_3week.dto.product.facade.ProductFacadeResponse;
import com.example.ecommerce_3week.facade.ProductFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductFacade productFacade;

    @PostMapping("/{id}")
    public ResponseEntity<ProductResponse>getProduct(@RequestBody ProductRequest request){
        ProductFacadeResponse response = productFacade.getProduct(request);
        return ResponseEntity.ok(new ProductResponse(response));
    }


}
