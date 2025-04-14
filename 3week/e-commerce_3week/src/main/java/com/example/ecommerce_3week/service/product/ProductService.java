package com.example.ecommerce_3week.service.product;

import com.example.ecommerce_3week.domain.order.OrderItem;
import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.product.ProductRepository;
import com.example.ecommerce_3week.dto.order.controller.OrderRequest;
import com.example.ecommerce_3week.dto.order.facade.OrderFacadeRequest;
import com.example.ecommerce_3week.dto.order.facade.PreparedOrderItems;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    //조회
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    public void save(List<Product> products) {
        for (Product product : products) {
            productRepository.save(product);
        }
    }

    public PreparedOrderItems prepareOrderItems(List<OrderFacadeRequest> itemRequests) {
        List<Product> products = new ArrayList<>();
        List<OrderItem> orderItems = itemRequests.stream()
                .map(request -> {
                    Product product = getProductById(request.getProductId());
                    product.deductStock(request.getQuantity());
                    products.add(product);
                    return new OrderItem(product.getId(), request.getQuantity(), product.getPrice());
                })
                .collect(Collectors.toList());
        return new PreparedOrderItems(products, orderItems);
    }



}
