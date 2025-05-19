package com.example.ecommerce.domain.order.event;

import com.example.ecommerce.api.order.dto.OrderCommand;

import java.util.List;

public class OrderEvent {//이벤트 정의
    private final Long userId;
    private final List<OrderCommand> orderCommands;

    public OrderEvent(Long userId, List<OrderCommand> orderCommands) {
        this.userId = userId;
        this.orderCommands = orderCommands;
    }

    public Long getUserId() { return userId; }
    public List<OrderCommand> getOrderCommands() { return orderCommands; }
}
