package com.example.ecommerce_3week.facade;

import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.dto.product.contorller.ProductRequest;
import com.example.ecommerce_3week.dto.product.facade.ProductFacadeResponse;
import com.example.ecommerce_3week.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;

    public ProductFacadeResponse getProduct(ProductRequest request){
        Product product = productService.getProductById(request.getProductId());
        return new ProductFacadeResponse(product.getId(), product.getPrice(), product.getStock());
    }
}
