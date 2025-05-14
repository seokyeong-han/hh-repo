package com.example.ecommerce.domain.ranking.entity;

import jakarta.persistence.*;
import lombok.Getter;

import javax.annotation.processing.Generated;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "daily_ranking")
public class DailyRankingJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ranking_date", nullable = false)
    private LocalDate rankingDate; //랭킹 집계 날짜

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "ranking_order", nullable = false) // 'rank'는 예약어이므로 name 변경
    private int rank;

    @Column(name = "score", nullable = false)
    private int score;

    protected DailyRankingJpaEntity() { /* JPA 용 */ }

    // 생성자 이름을 클래스명과 똑같이!
    public DailyRankingJpaEntity(LocalDate rankingDate, Long productId, int rank, int score) {
        this.rankingDate = rankingDate;
        this.productId  = productId;
        this.rank       = rank;
        this.score      = score;
    }


}
