package com.example.ecommerce_3week.service;

import com.example.ecommerce_3week.common.enums.PointTransactionType;
import com.example.ecommerce_3week.domain.pointhistory.PointHistory;
import com.example.ecommerce_3week.domain.pointhistory.PointHistoryRepository;
import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.service.pointhistory.PointHistoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class PointHistoryServiceTest {
    private PointHistoryRepository pointHistoryRepository;
    private PointHistoryService pointHistoryService;

    @BeforeEach
    void setUp() {
        pointHistoryRepository = mock(PointHistoryRepository.class);
        pointHistoryService = new PointHistoryService(pointHistoryRepository);
    }

    @Test
    @DisplayName("포인트 충전 내역 저장")
    void save_pointHistory_success() {
        // given
        User user = new User(1L, "testuser", 30_000L);

        // when
        pointHistoryService.chargeSave(user);

        // then
        verify(pointHistoryRepository, times(1)).save(argThat(history ->
                history.getUserId().equals(user.getId()) &&
                        history.getAmount().equals(user.getBalance()) &&
                        history.getType() == PointTransactionType.CHARGE
        ));
    }

    @Test
    @DisplayName("useSave: 포인트 사용 히스토리를 저장한다")
    void useSave_success() {
        // given
        Long userId = 1L;
        Long amount = 5000L;
        PointTransactionType type = PointTransactionType.USE;
        PointHistory history = new PointHistory(userId, amount, type);

        // when
        pointHistoryService.useSave(history);

        // then
        verify(pointHistoryRepository, times(1)).save(history);
    }
}
