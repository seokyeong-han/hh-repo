package com.example.ecommerce.global.listener;

import com.example.ecommerce.domain.order.event.OrderPlacedEvent;
import com.example.ecommerce.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RankingEventListener {
    private final RankingService rankingService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        for (Long productId : event.getProductIds()) {
            rankingService.recordView(productId);
        }
    }
}
