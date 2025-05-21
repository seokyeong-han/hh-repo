package com.example.ecommerce.domain.order.event;

import com.example.ecommerce.common.recode.ProceedOrderEvent;
import com.example.ecommerce.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Component
@RequiredArgsConstructor
public class OrderEventHandler {
    private final OrderService orderService;

    @EventListener
    public void handle(ProceedOrderEvent event) {
        orderService.eventPlaceOrder(event.userId(), event.items());
    }
}
