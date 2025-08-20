package com.heybro.heybro.onboarding.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OnboardingOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 온보딩 질문 식별키

    private String content; // 선택지 내용

    private int displayOrder; // 선택지 순서

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id") // question_id 이름으로 외래 키 컬럼 생성
    private OnboardingQuestion question;

    public void setOnboardingQuestion(OnboardingQuestion question) {
        this.question = question;
    }
}
