package com.example.ecommerce.event;

import com.example.ecommerce.common.recode.ProceedOrderEvent;
import com.example.ecommerce.domain.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class TestProceedOrderEventListener {
    private static final Logger log = LoggerFactory.getLogger(TestProceedOrderEventListener.class);

    private final List<ProceedOrderEvent> receivedEvents = new ArrayList<>();

    public List<ProceedOrderEvent> getReceivedEvents() {
        return receivedEvents;
    }

    @EventListener
    public void handle(ProceedOrderEvent event) {
        log.info("📨 이벤트 수신됨: userId=" + event.userId() + ", items=" + event.items().size());

        receivedEvents.add(event);
    }
}
