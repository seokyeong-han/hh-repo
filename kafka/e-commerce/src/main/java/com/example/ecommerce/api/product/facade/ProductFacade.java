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

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productService.findAll();
        return ProductResponse.from(products);
    }

}
