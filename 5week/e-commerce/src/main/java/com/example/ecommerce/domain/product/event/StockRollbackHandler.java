package com.example.ecommerce.domain.product.event;

import com.example.ecommerce.common.recode.StockRollbackRequestedEvent;
import com.example.ecommerce.domain.order.service.OrderService;
import com.example.ecommerce.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Component
@RequiredArgsConstructor
public class StockRollbackHandler {
    private static final Logger log = LoggerFactory.getLogger(StockRollbackHandler.class);


    private final ProductService productService;

    @EventListener
    public void handle(StockRollbackRequestedEvent event) {
        log.info("🔁 재고 롤백 시작: {}", event.items());
        productService.rollbackStock(event.items()); //이거 event용으로 새로 만들기
    }
}
