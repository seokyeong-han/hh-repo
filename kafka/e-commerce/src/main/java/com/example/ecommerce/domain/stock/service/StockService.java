package com.example.ecommerce.domain.stock.service;

import com.example.ecommerce.api.order.dto.ProductOrderItem;
import com.example.ecommerce.common.cache.CacheConstants;
import com.example.ecommerce.domain.event.OrderStartEvent;
import com.example.ecommerce.domain.product.dto.ProductOrderItemMessage;
import com.example.ecommerce.domain.product.repository.ProductRepository;
import com.example.ecommerce.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StockService {
    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    private final CacheManager cacheManager;
    private final ProductRepository productRepository;

    public static final String PRODUCT_DETAIL = CacheConstants.PRODUCT_DETAIL;


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

            cacheManager //캐시에서 이 ID의 상품 상세 데이터를 삭제
                    .getCache(PRODUCT_DETAIL)
                    .evict(item.getProductId());
        }
        log.info("✅ 재고 차감 완료 (orderId={})", orderId);

        return result;
    }


    //재고롤백
    @Transactional
    public void rollbackStock(List<ProductOrderItemMessage> items){
        for(ProductOrderItemMessage item : items){
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
            product.restoreStock(item.getQuantity());
            Product saved = productRepository.save(product);

            cacheManager //캐시에서 이 ID의 상품 상세 데이터를 삭제
                    .getCache(PRODUCT_DETAIL)
                    .evict(item.getProductId());
        }

    }
}
