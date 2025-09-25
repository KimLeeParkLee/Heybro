package com.heybro.heybro.skin.domain;

import com.heybro.heybro.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SkinDiagnosis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 피부 진단 식별키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 회원

    private LocalDateTime diagnosisDate; // 진단 날짜

    private int finalScore; // 종합 접수

    private int oilinessScore; // 유분 점수

    private int hydrationScore; // 수분 점수

    private int poreScore; // 모공 점수

    private int acneSocre; // 여드름 점수

    private String skinType; // 피부 타입
}
