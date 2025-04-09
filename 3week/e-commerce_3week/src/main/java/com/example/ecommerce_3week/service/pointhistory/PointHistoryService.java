package com.example.ecommerce_3week.service.pointhistory;

import com.example.ecommerce_3week.common.enums.PointTransactionType;
import com.example.ecommerce_3week.domain.pointhistory.PointHistory;
import com.example.ecommerce_3week.domain.pointhistory.PointHistoryRepository;
import com.example.ecommerce_3week.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;

    //저장
    public void save(User user){
        PointHistory history = new PointHistory(user.getId(), user.getBalance(), PointTransactionType.CHARGE);
        pointHistoryRepository.save(history);
    }


}
