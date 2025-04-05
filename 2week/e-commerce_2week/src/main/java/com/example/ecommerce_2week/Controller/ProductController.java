package com.example.ecommerce_2week.Controller;

import com.example.ecommerce_2week.DTO.ProductResponse;
import com.example.ecommerce_2week.entity.Product;
import com.example.ecommerce_2week.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@Tag(name = "Product API", description = "상품 관리 API")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/{id}")
    @Operation(summary = "상품 정보 조회", description = "상품 ID를 통해 정보를 조회합니다.")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id){
        Product product = productService.getProduct(id);
        ProductResponse response = new ProductResponse(product);
        return ResponseEntity.ok(response);
    }

}
