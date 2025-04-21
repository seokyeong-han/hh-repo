package com.example.ecommerce_3week.service.orderhistory;

import com.example.ecommerce_3week.domain.orderitem.OrderItem;
import com.example.ecommerce_3week.domain.orderhistory.OrderHistory;
import com.example.ecommerce_3week.domain.orderhistory.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;

    public void save(Long userId, Long orderId, List<OrderItem> items) {

        List<OrderHistory> histories = items.stream()
                .map(item -> new OrderHistory(
                        userId,
                        orderId,
                        item.getProductId(),
                        item.getQuantity(),
                        item.getTotalPrice()
                )).toList();

        orderHistoryRepository.saveAll(histories); //여러건 한번에 저장
    }

}
