package com.example.ecommerce.domain.product.event;

import com.example.ecommerce.common.recode.StockReserveRequestedEvent;
import com.example.ecommerce.common.recode.StockReservedEvent;
import com.example.ecommerce.common.recode.StockReservedItem;
import com.example.ecommerce.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Async
@Component
@RequiredArgsConstructor
public class StockReserveHandler {
    private final ProductService productService; //stockService로 변경하기
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void handle(StockReserveRequestedEvent event) {
        List<StockReservedItem> reserved = productService.reserveStock(event.items()); //재고차감

        eventPublisher.publishEvent(new StockReservedEvent( //주문 이벤트 발행
                event.userId(), reserved
        ));
    }
    // Spring이 StockReserveRequestedEvent 를 인자로 받는 @EventListener 를 찾아 자동 연결
    // @Async 붙여서 비동기 처리 (다른 쓰레드에서 실행됨)
    // 핸들러 클래스를 "이벤트 하나당 하나"로 쪼개면 단일 책임 원칙(SRP)이 유지됨
}
