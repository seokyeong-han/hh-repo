package com.example.ecommerce.common.recode;

import java.time.LocalDate;

public record DailyRanking (LocalDate rankingDate, Long productId, int rank, int score) {
}
