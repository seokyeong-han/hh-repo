package com.example.ecommerce.api.product.facade;

import com.example.ecommerce.api.product.dto.ProductResponse;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;

    //전체 상품 조회
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productService.findAll();
        return ProductResponse.from(products);
    }
    //단일 상품 조회
    public ProductResponse getProductById(Long id) {
        Product product = productService.findById(id);
        return ProductResponse.from(product);
    }

}
