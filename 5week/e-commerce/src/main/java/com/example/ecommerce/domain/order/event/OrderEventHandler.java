package com.example.ecommerce.domain.order.event;

import com.example.ecommerce.common.recode.ProceedOrderEvent;
import com.example.ecommerce.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Async
@Component
@RequiredArgsConstructor
public class OrderEventHandler {
    private final OrderService orderService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProceedOrderEvent event) {
        orderService.eventPlaceOrder(event.userId(), event.items());
    }
}
