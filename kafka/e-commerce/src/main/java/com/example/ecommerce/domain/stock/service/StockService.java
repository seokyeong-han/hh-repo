package com.example.ecommerce.domain.stock.service;

import com.example.ecommerce.api.order.dto.ProductOrderItem;
import com.example.ecommerce.domain.event.OrderStartEvent;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class StockService {
    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    private final ProductRepository productRepository;


    @Transactional
    public void reduceStock(OrderStartEvent event, String orderId){
        for(ProductOrderItem item : event.items()){
            Product product = productRepository.findWithPessimisticLockById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

            product.deductStock(item.getQuantity()); //도메인 로직 호출
            productRepository.save(product); // 변경사항 저장
        }
        log.info("✅ 재고 차감 완료 (orderId={})", orderId);


    }
}
