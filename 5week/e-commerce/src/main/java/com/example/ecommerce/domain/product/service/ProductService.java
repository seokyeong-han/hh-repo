package com.example.ecommerce.domain.product.service;

import com.example.ecommerce.api.order.dto.OrderCommand;
import com.example.ecommerce.api.product.dto.PreparedOrderItems;
import com.example.ecommerce.domain.order.model.OrderItem;
import com.example.ecommerce.domain.product.model.Product;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public PreparedOrderItems prepareOrderItems(List<OrderCommand> itemRequests) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderCommand command : itemRequests) {
            Product product = findById(command.getProductId());
            //재고 차감
            product.deductStock(command.getQuantity());
            // 총 가격 계산
            long pricePerItem = product.getPrice();
            long totalPrice = pricePerItem * command.getQuantity();
            //주문 아이템 생성
            OrderItem item = new OrderItem(
                    null, null, product.getId(), command.getQuantity(),
                    totalPrice, pricePerItem, LocalDateTime.now());
            orderItems.add(item);
            // 재고 저장
            save(product);
        }
        return new PreparedOrderItems(orderItems);
    }


}
