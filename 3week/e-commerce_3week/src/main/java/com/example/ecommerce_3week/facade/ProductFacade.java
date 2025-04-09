package com.example.ecommerce_3week.facade;

import com.example.ecommerce_3week.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;
}
