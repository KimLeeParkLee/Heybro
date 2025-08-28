package com.heybro.heybro.point.domain;

import com.heybro.heybro.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 포인트 변동 내역 식별키

    private int amount; // 금액

    private TransactionType transactionType; // 변동 유형

    private LocalDateTime transactionDate; // 변동 날짜

    private String description; // 변동 사유

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
