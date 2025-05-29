package com.example.ecommerce.domain.stock.service;

import com.example.ecommerce.api.order.dto.ProductOrderItem;
import com.example.ecommerce.domain.event.OrderStartEvent;
import com.example.ecommerce.domain.product.dto.ProductOrderItemMessage;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StockService {
    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    private final ProductRepository productRepository;


    @Transactional
    public List<ProductOrderItemMessage> reduceStock(OrderStartEvent event, String orderId){
        List<ProductOrderItemMessage> result = new ArrayList<>();

        for(ProductOrderItem item : event.items()){
            Product product = productRepository.findWithPessimisticLockById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

            product.deductStock(item.getQuantity()); //도메인 로직 호출
            productRepository.save(product); // 변경사항 저장

            result.add(new ProductOrderItemMessage(
                    item.getProductId(),
                    item.getQuantity(),
                    product.getPrice(),
                    product.getPrice() * item.getQuantity()
            ));
        }
        log.info("✅ 재고 차감 완료 (orderId={})", orderId);

        return result;
    }
}
