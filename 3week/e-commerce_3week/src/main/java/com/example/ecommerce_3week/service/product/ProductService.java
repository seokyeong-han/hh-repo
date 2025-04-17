package com.example.ecommerce_3week.service.product;

import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import com.example.ecommerce_3week.domain.product.Product;
import com.example.ecommerce_3week.domain.product.ProductRepository;
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

        // 먼저 상품 가져오고, 재고 차감
        for (OrderFacadeRequest request : itemRequests) {
            Product product = getProductById(request.getProductId());
            product.deductStock(request.getQuantity()); // 재고 차감
            products.add(product);
        }

        save(products);

        // OrderItem 생성
        List<OrderItem> orderItems = products.stream()
                .map(product -> {
                    // 해당 productId에 맞는 quantity 가져오기
                    int quantity = itemRequests.stream()
                            .filter(r -> r.getProductId().equals(product.getId()))
                            .findFirst()
                            .orElseThrow()
                            .getQuantity();

                    return new OrderItem(product.getId(), quantity, product.getPrice());
                })
                .collect(Collectors.toList());

        return new PreparedOrderItems(products, orderItems);
    }



}
