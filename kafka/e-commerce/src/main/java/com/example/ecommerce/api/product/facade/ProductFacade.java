package com.example.ecommerce.api.product.facade;

import com.example.ecommerce.api.product.dto.ProductResponse;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.service.ProductService;
import com.example.ecommerce.domain.ranking.PopularProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;
    private final PopularProductService popularProductService;

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productService.findAll();
        return ProductResponse.from(products);
    }

    //단일 상품 조회
    public ProductResponse getProductById(Long id) {
        Product product = productService.findById(id);
        //인기 상품 조회순 조회수 증가
        popularProductService.increaseViewCount(id);

        return ProductResponse.from(product);
    }

}
