package com.example.ecommerce.domain.product.service;

import com.example.ecommerce.api.order.dto.OrderCommand;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    //전체 상품 조회
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    //단일 상품 조회
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    public void saveAll(List<Product> products) {
        productRepository.saveAll(products);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    //재고 차감
    public List<Product> deductStocks(List<OrderCommand> itemRequests) {
        List<Product> products = new ArrayList<>();
        for (OrderCommand request : itemRequests) {
            Product product = findById(request.getProductId());
            product.deductStock(request.getQuantity());
            products.add(product);
            save(product); //재고 저장
        }
        return products; //구매 상품 return
    }


}
