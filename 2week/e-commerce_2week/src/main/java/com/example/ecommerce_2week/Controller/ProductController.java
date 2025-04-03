package com.example.ecommerce_2week.Controller;

import com.example.ecommerce_2week.DTO.ProductResponse;
import com.example.ecommerce_2week.entity.Product;
import com.example.ecommerce_2week.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id){
        Product product = productService.getProduct(id);
        ProductResponse response = new ProductResponse(product);
        return ResponseEntity.ok(response);
    }

}
