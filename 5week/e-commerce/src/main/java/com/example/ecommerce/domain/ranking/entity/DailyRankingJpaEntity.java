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
    private LocalDate rankingDate;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int rank;

    protected DailyRankingJpaEntity() { /* JPA 용 */ }

    // 생성자 이름을 클래스명과 똑같이!
    public DailyRankingJpaEntity(LocalDate rankingDate, Long productId, int rank) {
        this.rankingDate = rankingDate;
        this.productId  = productId;
        this.rank       = rank;
    }


}
