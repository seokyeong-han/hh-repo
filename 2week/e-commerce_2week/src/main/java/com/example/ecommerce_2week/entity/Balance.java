package com.example.ecommerce_2week.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "balance")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) //null 방지
    private Long amount = 0L; //기본값 설정

    private LocalDateTime updateDt;

    @OneToOne
    @JoinColumn(name = "user_id") // FK 설정
    private User user;
}
